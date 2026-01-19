package com.calyx.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_metadata")
data class SyncMetadata(
    @PrimaryKey
    val key: String, // e.g., "last_sync_timestamp"
    val value: Long
)
