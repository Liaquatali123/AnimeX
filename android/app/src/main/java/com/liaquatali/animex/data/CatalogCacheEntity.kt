package com.liaquatali.animex.data

import androidx.room.Entity

@Entity(tableName = "catalog_cache", primaryKeys = ["cacheKey", "page", "id"])
data class CatalogCacheEntity(
    val cacheKey: String,
    val page: Int,
    val id: String,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseDate: String?,
    val rating: Double?,
    val mediaType: String
) {
    fun toAnimeTitle() = AnimeTitle(id, title, overview, posterUrl, null, releaseDate, rating, mediaType)

    companion object {
        fun from(cacheKey: String, page: Int, item: AnimeTitle) = CatalogCacheEntity(
            cacheKey, page, item.id, item.title, item.overview, item.poster_url,
            item.release_date, item.rating, item.media_type
        )
    }
}
