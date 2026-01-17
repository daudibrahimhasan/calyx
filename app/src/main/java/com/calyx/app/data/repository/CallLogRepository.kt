package com.calyx.app.data.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.calyx.app.data.models.CallEntry
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.ContactInfo
import com.calyx.app.data.models.TimeRange
import com.calyx.app.utils.DateUtils
import com.calyx.app.utils.PhoneNumberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for accessing and processing call log data.
 */
class CallLogRepository(private val context: Context) {

    private val contentResolver: ContentResolver = context.contentResolver
    private val contactCache = mutableMapOf<String, ContactInfo?>()

    /**
     * Fetch call log entries from the system.
     */
    suspend fun fetchCallLog(timeRange: TimeRange): List<CallEntry> = withContext(Dispatchers.IO) {
        val calls = mutableListOf<CallEntry>()
        
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME
        )

        val selection = if (timeRange == TimeRange.WEEKLY) {
            "${CallLog.Calls.DATE} >= ?"
        } else null

        val selectionArgs = if (timeRange == TimeRange.WEEKLY) {
            arrayOf(DateUtils.getWeekStartTimestamp().toString())
        } else null

        val sortOrder = "${CallLog.Calls.DATE} DESC"

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

                while (it.moveToNext()) {
                    val phoneNumber = it.getString(numberIndex) ?: ""
                    
                    // Skip invalid numbers
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
                            contactName = it.getString(nameIndex)
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
     * Look up contact information for a phone number.
     */
    suspend fun lookupContact(phoneNumber: String): ContactInfo? = withContext(Dispatchers.IO) {
        val normalized = PhoneNumberUtils.normalize(phoneNumber)
        
        // Check cache first
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

        // Cache the result
        contactCache[normalized] = contactInfo
        contactInfo
    }

    /**
     * Aggregate call entries into caller statistics.
     */
    suspend fun aggregateStats(calls: List<CallEntry>): List<CallerStats> = withContext(Dispatchers.IO) {
        val statsMap = mutableMapOf<String, MutableCallerStats>()

        for (call in calls) {
            val normalized = if (PhoneNumberUtils.isPrivateNumber(call.phoneNumber)) {
                "PRIVATE"
            } else {
                PhoneNumberUtils.normalize(call.phoneNumber)
            }

            val stats = statsMap.getOrPut(normalized) {
                MutableCallerStats(
                    phoneNumber = call.phoneNumber,
                    displayName = call.contactName ?: call.phoneNumber
                )
            }

            stats.totalCalls++
            stats.totalDuration += call.duration

            when (call.callType) {
                CallEntry.TYPE_INCOMING -> stats.incomingCalls++
                CallEntry.TYPE_OUTGOING -> stats.outgoingCalls++
                CallEntry.TYPE_MISSED -> stats.missedCalls++
            }

            if (stats.firstCallDate == 0L || call.date < stats.firstCallDate) {
                stats.firstCallDate = call.date
            }
            if (call.date > stats.lastCallDate) {
                stats.lastCallDate = call.date
            }
        }

        // Enrich with contact info and convert to immutable
        statsMap.values.map { mutable ->
            val contactInfo = lookupContact(mutable.phoneNumber)
            
            val displayName = when {
                contactInfo != null -> contactInfo.displayName
                PhoneNumberUtils.isPrivateNumber(mutable.phoneNumber) -> "Private Number"
                mutable.displayName.isNotBlank() && mutable.displayName != mutable.phoneNumber -> mutable.displayName
                else -> PhoneNumberUtils.formatForDisplay(mutable.phoneNumber)
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
                averageDuration = if (mutable.totalCalls > 0) mutable.totalDuration / mutable.totalCalls else 0,
                firstCallDate = mutable.firstCallDate,
                lastCallDate = mutable.lastCallDate
            )
        }
    }

    /**
     * Rank caller stats by call count.
     */
    fun rankByCount(stats: List<CallerStats>): List<CallerStats> {
        return stats.sortedByDescending { it.totalCalls }
            .mapIndexed { index, caller ->
                caller.copy(rankByCount = index + 1)
            }
    }

    /**
     * Rank caller stats by total duration.
     */
    fun rankByDuration(stats: List<CallerStats>): List<CallerStats> {
        return stats.sortedByDescending { it.totalDuration }
            .mapIndexed { index, caller ->
                caller.copy(rankByDuration = index + 1)
            }
    }

    /**
     * Get fully processed and ranked caller statistics.
     */
    suspend fun getCallerStats(timeRange: TimeRange): List<CallerStats> {
        val calls = fetchCallLog(timeRange)
        val stats = aggregateStats(calls)
        
        // Apply both rankings
        val rankedByCount = rankByCount(stats)
        val durationRankMap = rankByDuration(stats).associate { it.phoneNumber to it.rankByDuration }
        
        return rankedByCount.map { caller ->
            caller.copy(rankByDuration = durationRankMap[caller.phoneNumber] ?: 0)
        }
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
