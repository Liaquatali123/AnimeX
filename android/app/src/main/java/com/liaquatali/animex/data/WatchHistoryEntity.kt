package com.liaquatali.animex.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val mediaUri: String,
    val displayName: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long
)
