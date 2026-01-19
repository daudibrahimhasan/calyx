package com.calyx.app.data.local.dao

import androidx.room.*
import com.calyx.app.data.local.entities.CallerStatsEntity
import com.calyx.app.data.local.entities.DailyStatsEntity
import com.calyx.app.data.local.entities.SyncMetadata
import kotlinx.coroutines.flow.Flow

@Dao
interface CallerStatsDao {
    @Query("SELECT * FROM caller_stats")
    fun getAllStats(): Flow<List<CallerStatsEntity>>

    @Query("SELECT * FROM caller_stats")
    suspend fun getAllStatsList(): List<CallerStatsEntity>

    @Query("SELECT * FROM caller_stats WHERE phoneNumber = :phoneNumber")
    suspend fun getStatsForNumber(phoneNumber: String): CallerStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: List<CallerStatsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStat(stat: CallerStatsEntity)

    @Query("DELETE FROM caller_stats")
    suspend fun clearAll()

    // Metadata
    @Query("SELECT value FROM sync_metadata WHERE `key` = :key")
    suspend fun getMetadata(key: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMetadata(metadata: SyncMetadata)

    @Transaction
    suspend fun clearAndReset() {
        clearAll()
        clearDailyStats()
        updateMetadata(SyncMetadata("last_sync_timestamp", 0L))
    }

    // Daily Stats
    @Query("SELECT * FROM daily_stats WHERE dateTimestamp >= :since ORDER BY dateTimestamp ASC")
    suspend fun getDailyStatsSince(since: Long): List<DailyStatsEntity>

    @Query("SELECT * FROM daily_stats WHERE dateTimestamp = :date")
    suspend fun getDailyStat(date: Long): DailyStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyStats(stats: List<DailyStatsEntity>)

    @Query("DELETE FROM daily_stats")
    suspend fun clearDailyStats()
}
