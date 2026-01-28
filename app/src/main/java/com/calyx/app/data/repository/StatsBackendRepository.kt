package com.calyx.app.data.repository

import com.calyx.app.data.models.GlobalStats
import com.calyx.app.data.models.PeriodStats
import com.calyx.app.data.models.UserBackendStats
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Handles the specific "Simply JSON" backend logic.
 * 
 * Logic Flow:
 * 1. Count calls locally (passed from CallLogRepository)
 * 2. Sync with Firebase (update user total, update global aggregators) - ONLY IF AVAILABLE
 * 3. Fetch global stats for comparison
 * 
 * This is free because Firebase is optional - app works 100% offline.
 */
class StatsBackendRepository {

    // Firebase is optional - only initialize if available
    private val firebaseAvailable by lazy { FirebaseAvailability.isFirebaseAvailable() }
    
    private val db by lazy { 
        if (firebaseAvailable) {
            try {
                FirebaseDatabase.getInstance("https://calyz-17b77-default-rtdb.asia-southeast1.firebasedatabase.app/")
            } catch (e: Exception) {
                null
            }
        } else null
    }
    
    private val globalRef by lazy { db?.getReference("calyz-stats/global_stats") }
    private val usersRef by lazy { db?.getReference("calyz-stats/users") }

    private val _globalStats = MutableStateFlow(GlobalStats())
    val globalStats: StateFlow<GlobalStats> = _globalStats
    
    @Volatile
    private var listenerAttached = false
    
    /**
     * Check if Firebase backend is available for use.
     */
    fun isAvailable(context: android.content.Context): Boolean {
        return FirebaseAvailability.canUseFirebase(context)
    }

    /**
     * Start listening for global stats updates.
     * Call this from a background coroutine to avoid blocking the main thread.
     * 
     * This is optional - only works when Firebase is available.
     */
    fun startListening() {
        if (!firebaseAvailable || globalRef == null) return
        if (listenerAttached) return
        listenerAttached = true
        
        try {
            // Listen for real-time global stats updates
            globalRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val stats = snapshot.getValue(GlobalStats::class.java)
                        if (stats != null) {
                            _globalStats.value = stats
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error silently - offline mode is fine
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Master Sync Function - Updates everything in one go.
     * 
     * This is optional - if Firebase is unavailable, stats remain local-only.
     * The app works perfectly without any backend sync.
     */
    suspend fun syncStats(
        context: android.content.Context,
        userId: String,
        localTotalCalls: Int,
        localTodayCalls: Int,
        localWeekCalls: Int
    ) = withContext(Dispatchers.IO) {
        // Graceful degradation: skip sync if Firebase is unavailable
        if (!firebaseAvailable || globalRef == null || usersRef == null) {
            return@withContext
        }
        
        try {
            val prefs = context.getSharedPreferences("calyz_stats_sync", android.content.Context.MODE_PRIVATE)
            val todayStr = getTodayDateString()
            val weekStartStr = getMondayDateString()

            // 1. Calculate how much NEW data we have to contribute since last sync
            val lastSyncedTotal = prefs.getInt("last_total", 0)
            val lastSyncedDay = prefs.getString("last_day_str", "")
            val lastSyncedWeek = prefs.getString("last_week_str", "")
            
            // If the day/week has changed, we reset our local "already synced" counters
            val syncedTodaySoFar = if (lastSyncedDay == todayStr) prefs.getInt("synced_today", 0) else 0
            val syncedWeekSoFar = if (lastSyncedWeek == weekStartStr) prefs.getInt("synced_week", 0) else 0

            val totalDelta = (localTotalCalls - lastSyncedTotal).coerceAtLeast(0)
            val todayContribution = (localTodayCalls - syncedTodaySoFar).coerceAtLeast(0)
            val weekContribution = (localWeekCalls - syncedWeekSoFar).coerceAtLeast(0)

            if (totalDelta == 0 && todayContribution == 0 && weekContribution == 0) return@withContext

            // 2. Perform Transactional Update
            globalRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val stats = currentData.getValue(GlobalStats::class.java) ?: GlobalStats()
                    
                    // Total Users increment
                    val isNewUser = lastSyncedTotal == 0
                    val newTotalUsers = if (isNewUser) stats.total_users + 1 else stats.total_users

                    // Today Logic
                    var today = stats.today
                    if (today.date != todayStr) {
                        today = PeriodStats(date = todayStr, calls = 0, active_users = 0)
                    }
                    val newTodayActive = if (todayContribution > 0) today.active_users + 1 else today.active_users

                    // Week Logic
                    var week = stats.week
                    if (week.week_start != weekStartStr) {
                        week = PeriodStats(week_start = weekStartStr, calls = 0, active_users = 0)
                    }
                    val newWeekActive = if (weekContribution > 0) week.active_users + 1 else week.active_users

                    currentData.value = stats.copy(
                        total_users = newTotalUsers,
                        total_global_calls = stats.total_global_calls + totalDelta,
                        today = today.copy(
                            calls = today.calls + todayContribution,
                            active_users = newTodayActive
                        ),
                        week = week.copy(
                            calls = week.calls + weekContribution,
                            active_users = newWeekActive
                        )
                    )
                    return Transaction.success(currentData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                    if (committed) {
                        // 3. Update local markers ONLY on success
                        prefs.edit()
                            .putInt("last_total", localTotalCalls)
                            .putInt("synced_today", localTodayCalls)
                            .putInt("synced_week", localWeekCalls)
                            .putString("last_day_str", todayStr)
                            .putString("last_week_str", weekStartStr)
                            .apply()
                        
                        // Also update individual user node
                        usersRef.child(userId).setValue(
                            UserBackendStats(localTotalCalls, System.currentTimeMillis())
                        )
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    private fun getMondayDateString(): String {
        val cal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"), Locale.US)
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(cal.time)
    }
}
