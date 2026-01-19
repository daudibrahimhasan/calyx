package com.calyx.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caller_stats")
data class CallerStatsEntity(
    @PrimaryKey
    val phoneNumber: String, // Normalized phone number
    val contactId: String? = null,
    val displayName: String,
    val profilePhotoUri: String? = null,
    val totalCalls: Int = 0,
    val incomingCalls: Int = 0,
    val outgoingCalls: Int = 0,
    val missedCalls: Int = 0,
    val totalDuration: Long = 0L,
    val firstCallDate: Long = 0L,
    val lastCallDate: Long = 0L
)
