package com.calyx.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey
    val dateTimestamp: Long, // Start of the day in milliseconds
    val totalCalls: Int = 0,
    val totalDuration: Long = 0L,
    val incomingCalls: Int = 0,
    val outgoingCalls: Int = 0,
    val missedCalls: Int = 0
)
