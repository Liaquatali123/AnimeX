package com.liaquatali.animex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun remove(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun contains(id: String): Boolean
}
