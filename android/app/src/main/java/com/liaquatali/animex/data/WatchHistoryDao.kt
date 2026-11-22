package com.liaquatali.animex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY updatedAt DESC")
    fun observeRecent(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE mediaUri = :uri LIMIT 1")
    suspend fun find(uri: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(item: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE mediaUri = :uri")
    suspend fun delete(uri: String)
}
