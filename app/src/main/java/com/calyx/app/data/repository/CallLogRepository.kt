package com.calyx.app.data.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.calyx.app.data.local.CalyxDatabase
import com.calyx.app.data.local.entities.CallerStatsEntity
import com.calyx.app.data.local.entities.DailyStatsEntity
import com.calyx.app.data.local.entities.SyncMetadata
import com.calyx.app.data.models.CallEntry
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.ContactInfo
import com.calyx.app.data.models.TimeRange
import com.calyx.app.utils.DateUtils
import com.calyx.app.utils.PhoneNumberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Repository for accessing and processing call log data.
 */
class CallLogRepository(private val context: Context) {

    private val contentResolver: ContentResolver = context.contentResolver
    private val contactCache = mutableMapOf<String, ContactInfo?>()
    private val database = CalyxDatabase.getDatabase(context)
    private val dao = database.callerStatsDao()
    private val syncMutex = Mutex()

    /**
     * Fetch call log entries from the system.
     * @param sinceTimestamp if provided, only fetch calls after this date.
     */
    suspend fun fetchCallLog(timeRange: TimeRange, sinceTimestamp: Long = 0L): List<CallEntry> = withContext(Dispatchers.IO) {
        val calls = mutableListOf<CallEntry>()
        
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.CACHED_PHOTO_URI
        )

        var selection = if (timeRange == TimeRange.WEEKLY) {
            "${CallLog.Calls.DATE} >= ?"
        } else null

        var selectionArgs = if (timeRange == TimeRange.WEEKLY) {
            arrayOf(DateUtils.getWeekStartTimestamp().toString())
        } else null

        // Add incremental sync selection if sinceTimestamp is provided
        if (sinceTimestamp > 0) {
            val incrementalSelection = "${CallLog.Calls.DATE} > ?"
            selection = if (selection != null) "$selection AND $incrementalSelection" else incrementalSelection
            selectionArgs = if (selectionArgs != null) selectionArgs + sinceTimestamp.toString() else arrayOf(sinceTimestamp.toString())
        }

        val sortOrder = "${CallLog.Calls.DATE} ASC" // ASC is better for incremental processing

        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.let {
                val idIndex = it.getColumnIndexOrThrow(CallLog.Calls._ID)
                val numberIndex = it.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                val typeIndex = it.getColumnIndexOrThrow(CallLog.Calls.TYPE)
                val dateIndex = it.getColumnIndexOrThrow(CallLog.Calls.DATE)
                val durationIndex = it.getColumnIndexOrThrow(CallLog.Calls.DURATION)
                val nameIndex = it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
                val photoIndex = it.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI)

                while (it.moveToNext()) {
                    val phoneNumber = it.getString(numberIndex) ?: ""
                    
                    if (!PhoneNumberUtils.isValidPhoneNumber(phoneNumber) && 
                        !PhoneNumberUtils.isPrivateNumber(phoneNumber)) {
                        continue
                    }

                    calls.add(
                        CallEntry(
                            id = it.getLong(idIndex),
                            phoneNumber = phoneNumber,
                            callType = it.getInt(typeIndex),
                            date = it.getLong(dateIndex),
                            duration = it.getLong(durationIndex),
                            contactName = it.getString(nameIndex),
                            photoUri = if (photoIndex != -1) it.getString(photoIndex) else null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        calls
    }

    /**
     * Incremental sync of call stats into local Room DB.
     * Synchronizes both CallerStats (per person) and DailyStats (per day).
     */
    private suspend fun syncCallerStats() = syncMutex.withLock {
        withContext(Dispatchers.IO) {
            val lastSync = dao.getMetadata("last_sync_timestamp") ?: 0L
            val newCalls = fetchCallLog(TimeRange.ALL_TIME, sinceTimestamp = lastSync)
            
            if (newCalls.isEmpty()) return@withContext

            // Load all relevant daily stats into memory first to avoid N+1 queries
            val earliestNewCall = newCalls.first().date
            val dailyStatsMap = dao.getDailyStatsSince(getStartOfDay(earliestNewCall))
                .associateBy { it.dateTimestamp }
                .toMutableMap()

            // Load existing caller stats into a mutable map for quick updates
            // For large databases, we might want to only load callers present in newCalls
            val existingEntities = dao.getAllStatsList()
            val callerStatsMap = existingEntities.associateBy { it.phoneNumber }.toMutableMap()
            
            var maxTimestamp = lastSync

            for (call in newCalls) {
                // 1. Update Caller Stats
                val normalized = if (PhoneNumberUtils.isPrivateNumber(call.phoneNumber)) {
                    "PRIVATE"
                } else {
                    PhoneNumberUtils.normalize(call.phoneNumber)
                }

                val currentCaller = callerStatsMap[normalized] ?: CallerStatsEntity(
                    phoneNumber = normalized,
                    displayName = (if (!call.contactName.isNullOrEmpty()) call.contactName else null) ?: call.phoneNumber,
                    contactId = null,
                    profilePhotoUri = call.photoUri,
                    firstCallDate = call.date
                )

                callerStatsMap[normalized] = currentCaller.copy(
                    totalCalls = currentCaller.totalCalls + 1,
                    incomingCalls = currentCaller.incomingCalls + if (call.callType == CallEntry.TYPE_INCOMING) 1 else 0,
                    outgoingCalls = currentCaller.outgoingCalls + if (call.callType == CallEntry.TYPE_OUTGOING) 1 else 0,
                    missedCalls = currentCaller.missedCalls + if (isMissed(call.callType)) 1 else 0,
                    totalDuration = currentCaller.totalDuration + call.duration,
                    lastCallDate = maxOf(currentCaller.lastCallDate, call.date),
                    firstCallDate = if (currentCaller.firstCallDate == 0L) call.date else minOf(currentCaller.firstCallDate, call.date),
                    displayName = if (!call.contactName.isNullOrEmpty()) call.contactName else currentCaller.displayName,
                    profilePhotoUri = if (!call.photoUri.isNullOrEmpty()) call.photoUri else currentCaller.profilePhotoUri
                )

                // 2. Update Daily Stats
                val dayStart = getStartOfDay(call.date)
                val currentDaily = dailyStatsMap[dayStart] ?: DailyStatsEntity(dateTimestamp = dayStart)
                
                dailyStatsMap[dayStart] = currentDaily.copy(
                    totalCalls = currentDaily.totalCalls + 1,
                    totalDuration = currentDaily.totalDuration + call.duration,
                    incomingCalls = currentDaily.incomingCalls + if (call.callType == CallEntry.TYPE_INCOMING) 1 else 0,
                    outgoingCalls = currentDaily.outgoingCalls + if (call.callType == CallEntry.TYPE_OUTGOING) 1 else 0,
                    missedCalls = currentDaily.missedCalls + if (isMissed(call.callType)) 1 else 0
                )
                
                if (call.date > maxTimestamp) maxTimestamp = call.date
            }

            // Save updated stats back to DB
            dao.upsertStats(callerStatsMap.values.toList())
            dao.upsertDailyStats(dailyStatsMap.values.toList())
            
            // Update sync metadata
            dao.updateMetadata(SyncMetadata("last_sync_timestamp", maxTimestamp))
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun isMissed(type: Int): Boolean {
        return type == CallEntry.TYPE_MISSED || type == CallEntry.TYPE_VOICEMAIL || type == CallEntry.TYPE_REJECTED
    }

    /**
     * Look up contact information for a phone number.
     */
    suspend fun lookupContact(phoneNumber: String): ContactInfo? = withContext(Dispatchers.IO) {
        val normalized = PhoneNumberUtils.normalize(phoneNumber)
        
        if (contactCache.containsKey(normalized)) {
            return@withContext contactCache[normalized]
        }

        var contactInfo: ContactInfo? = null
        
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )

            val projection = arrayOf(
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.PHOTO_URI
            )

            val cursor = contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID)
                    val nameIndex = it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    val photoIndex = it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI)

                    contactInfo = ContactInfo(
                        contactId = it.getString(idIndex),
                        displayName = it.getString(nameIndex) ?: phoneNumber,
                        photoUri = it.getString(photoIndex)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        contactCache[normalized] = contactInfo
        contactInfo
    }

    /**
     * Map Entity to Model.
     */
    private fun mapToModel(entity: CallerStatsEntity): CallerStats {
        return CallerStats(
            phoneNumber = entity.phoneNumber,
            contactId = entity.contactId,
            displayName = entity.displayName,
            profilePhotoUri = entity.profilePhotoUri,
            totalCalls = entity.totalCalls,
            incomingCalls = entity.incomingCalls,
            outgoingCalls = entity.outgoingCalls,
            missedCalls = entity.missedCalls,
            totalDuration = entity.totalDuration,
            averageDuration = if (entity.totalCalls > 0) entity.totalDuration / entity.totalCalls else 0L,
            firstCallDate = entity.firstCallDate,
            lastCallDate = entity.lastCallDate
        )
    }

    /**
     * Get fully processed and ranked caller statistics.
     * Uses the optimized Room cache for ALL_TIME range.
     */
    suspend fun getCallerStats(timeRange: TimeRange): List<CallerStats> {
        if (timeRange == TimeRange.ALL_TIME) {
            // Incremental sync - only fetches new calls!
            syncCallerStats()
            
            // Fetch from Room DB
            val cachedEntities = dao.getAllStatsList()
            var stats = cachedEntities.map { mapToModel(it) }
            
            // Enrich with contactId and real names (if missing in cache)
            // Note: This is still fast because we can background it or rely on existing cache
            stats = stats.map { caller ->
                if (caller.contactId == null && !PhoneNumberUtils.isPrivateNumber(caller.phoneNumber)) {
                    val info = lookupContact(caller.phoneNumber)
                    caller.copy(
                        contactId = info?.contactId,
                        displayName = info?.displayName ?: caller.displayName,
                        profilePhotoUri = info?.photoUri ?: caller.profilePhotoUri
                    )
                } else caller
            }

            val mergedStats = mergeByContactId(stats)
            return applyRankings(mergedStats)
        } else {
            // Weekly is still fast via direct query
            val calls = fetchCallLog(timeRange)
            val stats = aggregateStats(calls)
            return applyRankings(stats)
        }
    }

    private fun applyRankings(stats: List<CallerStats>): List<CallerStats> {
        val rankedByCount = rankByCount(stats)
        val durationRankMap = rankByDuration(stats).associate { it.phoneNumber to it.rankByDuration }
        
        return rankedByCount.map { caller ->
            caller.copy(rankByDuration = durationRankMap[caller.phoneNumber] ?: 0)
        }
    }

    /**
     * Aggregate call entries into caller statistics (Original logic for Weekly/Temp).
     */
    suspend fun aggregateStats(calls: List<CallEntry>): List<CallerStats> = withContext(Dispatchers.IO) {
        val statsMap = mutableMapOf<String, MutableCallerStats>()
        val cachedNames = mutableMapOf<String, String>()
        val cachedPhotos = mutableMapOf<String, String>()

        for (call in calls) {
            val normalized = if (PhoneNumberUtils.isPrivateNumber(call.phoneNumber)) "PRIVATE" else PhoneNumberUtils.normalize(call.phoneNumber)
            val stats = statsMap.getOrPut(normalized) {
                MutableCallerStats(phoneNumber = call.phoneNumber, displayName = call.contactName ?: call.phoneNumber)
            }
            if (!call.contactName.isNullOrEmpty()) cachedNames[normalized] = call.contactName
            if (!call.photoUri.isNullOrEmpty()) cachedPhotos[normalized] = call.photoUri

            stats.totalCalls++
            stats.totalDuration += call.duration
            if (call.duration > 0) stats.connectedCallCount++

            when (call.callType) {
                CallEntry.TYPE_INCOMING -> stats.incomingCalls++
                CallEntry.TYPE_OUTGOING -> stats.outgoingCalls++
                CallEntry.TYPE_MISSED, CallEntry.TYPE_VOICEMAIL, CallEntry.TYPE_REJECTED -> stats.missedCalls++
            }

            if (stats.firstCallDate == 0L || call.date < stats.firstCallDate) stats.firstCallDate = call.date
            if (call.date > stats.lastCallDate) stats.lastCallDate = call.date
        }

        val enrichedStats = statsMap.values.map { mutable ->
            val normalized = if (PhoneNumberUtils.isPrivateNumber(mutable.phoneNumber)) "PRIVATE" else PhoneNumberUtils.normalize(mutable.phoneNumber)
            var contactInfo = contactCache[normalized]
            
            if (contactInfo == null) {
                val logName = cachedNames[normalized]
                val logPhoto = cachedPhotos[normalized]
                if (logName != null) {
                    contactInfo = ContactInfo(contactId = null, displayName = logName, photoUri = logPhoto)
                    contactCache[normalized] = contactInfo
                } else {
                    contactInfo = lookupContact(mutable.phoneNumber)
                }
            }
            
            val displayName = when {
                contactInfo?.displayName != null -> contactInfo!!.displayName
                PhoneNumberUtils.isPrivateNumber(mutable.phoneNumber) -> "Private Number"
                mutable.displayName.isNotBlank() && mutable.displayName != mutable.phoneNumber -> mutable.displayName
                else -> mutable.phoneNumber
            }
            
            CallerStats(
                contactId = contactInfo?.contactId,
                phoneNumber = mutable.phoneNumber,
                displayName = displayName,
                profilePhotoUri = contactInfo?.photoUri,
                totalCalls = mutable.totalCalls,
                incomingCalls = mutable.incomingCalls,
                outgoingCalls = mutable.outgoingCalls,
                missedCalls = mutable.missedCalls,
                totalDuration = mutable.totalDuration,
                averageDuration = if (mutable.incomingCalls + mutable.outgoingCalls > 0) 
                    mutable.totalDuration / (mutable.incomingCalls + mutable.outgoingCalls) else 0L,
                firstCallDate = mutable.firstCallDate,
                lastCallDate = mutable.lastCallDate
            )
        }

        mergeByContactId(enrichedStats)
    }

    /**
     * Merge CallerStats entries that belong to the same contact (have the same contactId).
     */
    private fun mergeByContactId(stats: List<CallerStats>): List<CallerStats> {
        val withContactId = stats.filter { it.contactId != null }
        val withoutContactId = stats.filter { it.contactId == null }

        val mergedByContact = withContactId
            .groupBy { it.contactId!! }
            .map { (_, entries) ->
                if (entries.size == 1) entries.first() else mergeCallerStats(entries)
            }

        return mergedByContact + withoutContactId
    }

    private fun mergeCallerStats(entries: List<CallerStats>): CallerStats {
        val primary = entries.maxByOrNull { it.totalCalls } ?: entries.first()
        return CallerStats(
            contactId = primary.contactId,
            phoneNumber = primary.phoneNumber,
            displayName = primary.displayName,
            profilePhotoUri = primary.profilePhotoUri,
            totalCalls = entries.sumOf { it.totalCalls },
            incomingCalls = entries.sumOf { it.incomingCalls },
            outgoingCalls = entries.sumOf { it.outgoingCalls },
            missedCalls = entries.sumOf { it.missedCalls },
            totalDuration = entries.sumOf { it.totalDuration },
            averageDuration = run {
                val totalConnected = entries.sumOf { it.incomingCalls + it.outgoingCalls }
                if (totalConnected > 0) entries.sumOf { it.totalDuration } / totalConnected else 0
            },
            firstCallDate = entries.minOf { it.firstCallDate },
            lastCallDate = entries.maxOf { it.lastCallDate }
        )
    }

    fun rankByCount(stats: List<CallerStats>): List<CallerStats> {
        return stats.sortedByDescending { it.totalCalls }.mapIndexed { index, caller -> caller.copy(rankByCount = index + 1) }
    }

    fun rankByDuration(stats: List<CallerStats>): List<CallerStats> {
        return stats.sortedByDescending { it.totalDuration }.mapIndexed { index, caller -> caller.copy(rankByDuration = index + 1) }
    }


    /**
     * Calculate summary statistics.
     */
    fun calculateSummary(stats: List<CallerStats>): CallSummary {
        return CallSummary(
            totalCalls = stats.sumOf { it.totalCalls },
            totalDuration = stats.sumOf { it.totalDuration },
            uniqueContacts = stats.size,
            totalIncoming = stats.sumOf { it.incomingCalls },
            totalOutgoing = stats.sumOf { it.outgoingCalls },
            totalMissed = stats.sumOf { it.missedCalls }
        )
    }
    /**
     * Get daily call counts for the last N days (for activity heatmap).
     * Returns a list of integers representing call counts for each day, ordered from oldest to newest.
     * Index 0 = (days-1) days ago, Index (days-1) = Today.
     * 
     * COMPONENT PERFORMANCE: O(1) retrieval from Room DB.
     */
    suspend fun getDailyCallCounts(days: Int = 35): List<Int> = withContext(Dispatchers.IO) {
        // Ensure data is synced before reading
        syncCallerStats()
        
        val counts = IntArray(days)
        val DAY_IN_MS = 24 * 60 * 60 * 1000L
        
        // Calculate the start timestamp (N days ago at midnight)
        val todayStart = getStartOfDay(System.currentTimeMillis())
        val startTimestamp = todayStart - ((days - 1) * DAY_IN_MS)
        
        val dailyStats = dao.getDailyStatsSince(startTimestamp)
        
        for (stat in dailyStats) {
            val daysDiff = ((stat.dateTimestamp - startTimestamp) / DAY_IN_MS).toInt()
            if (daysDiff in 0 until days) {
                counts[daysDiff] = stat.totalCalls
            }
        }
        
        counts.toList()
    }

    /**
     * Get weekly call counts for trend analysis.
     * Returns a list of 7 integers representing calls for each day of the current week (Mon-Sun).
     * 
     * COMPONENT PERFORMANCE: O(1).
     */
    suspend fun getWeeklyCallCounts(): List<Int> = withContext(Dispatchers.IO) {
        val weeklyCounts = IntArray(7)
        val DAY_IN_MS = 24 * 60 * 60 * 1000L
        
        // Calculate the start of this week (Monday)
        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == java.util.Calendar.SUNDAY) 6 else dayOfWeek - 2
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -daysFromMonday)
        val weekStartTimestamp = getStartOfDay(calendar.timeInMillis)
        
        val dailyStats = dao.getDailyStatsSince(weekStartTimestamp)
        
        for (stat in dailyStats) {
            val daysDiff = ((stat.dateTimestamp - weekStartTimestamp) / DAY_IN_MS).toInt()
            if (daysDiff in 0..6) {
                weeklyCounts[daysDiff] = stat.totalCalls
            }
        }
        
        weeklyCounts.toList()
    }

    /**
     * Get last week's call counts for trend comparison.
     * 
     * COMPONENT PERFORMANCE: O(1).
     */
    suspend fun getLastWeekCallCounts(): List<Int> = withContext(Dispatchers.IO) {
        val weeklyCounts = IntArray(7)
        val DAY_IN_MS = 24 * 60 * 60 * 1000L
        
        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == java.util.Calendar.SUNDAY) 6 else dayOfWeek - 2
        
        // Go to start of last week
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -daysFromMonday - 7)
        val lastWeekStart = getStartOfDay(calendar.timeInMillis)
        
        val dailyStats = dao.getDailyStatsSince(lastWeekStart)
        
        for (stat in dailyStats) {
            val daysDiff = ((stat.dateTimestamp - lastWeekStart) / DAY_IN_MS).toInt()
            if (daysDiff in 0..6) {
                weeklyCounts[daysDiff] = stat.totalCalls
            }
        }
        
        weeklyCounts.toList()
    }

    /**
     * Clear all cached and local database data.
     */
    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        contactCache.clear()
        dao.clearAndReset()
    }

    /**
     * Clear the contact cache.
     */
    fun clearCache() {
        contactCache.clear()
    }

    /**
     * Mutable helper class for aggregation.
     */
    private data class MutableCallerStats(
        var phoneNumber: String,
        var displayName: String,
        var totalCalls: Int = 0,
        var connectedCallCount: Int = 0,
        var incomingCalls: Int = 0,
        var outgoingCalls: Int = 0,
        var missedCalls: Int = 0,
        var totalDuration: Long = 0L,
        var firstCallDate: Long = 0L,
        var lastCallDate: Long = 0L
    )
}

/**
 * Summary statistics for all calls.
 */
data class CallSummary(
    val totalCalls: Int,
    val totalDuration: Long,
    val uniqueContacts: Int,
    val totalIncoming: Int,
    val totalOutgoing: Int,
    val totalMissed: Int
)
