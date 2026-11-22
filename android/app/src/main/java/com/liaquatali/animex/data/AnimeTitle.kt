package com.liaquatali.animex.data

data class AnimeTitle(
    val id: String,
    val title: String,
    val overview: String = "",
    val poster_url: String? = null,
    val backdrop_url: String? = null,
    val release_date: String? = null,
    val rating: Double? = null,
    val media_type: String = "movie",
    val genre_ids: List<Int> = emptyList()
)

data class CatalogResponse(
    val items: List<AnimeTitle>,
    val page: Int,
    val limit: Int? = null,
    val total: Int,
    val has_next: Boolean,
    val source: String,
    val message: String? = null
)
