package com.liaquatali.animex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CatalogCacheDao {
    @Query("SELECT * FROM catalog_cache WHERE cacheKey = :cacheKey AND page = :page")
    suspend fun getPage(cacheKey: String, page: Int): List<CatalogCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CatalogCacheEntity>)

    @Query("DELETE FROM catalog_cache WHERE cacheKey = :cacheKey")
    suspend fun clear(cacheKey: String)
}
