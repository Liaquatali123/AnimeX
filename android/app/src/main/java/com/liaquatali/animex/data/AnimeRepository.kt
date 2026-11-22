package com.liaquatali.animex.data

class AnimeRepository(private val api: CatalogApi = NetworkModule.api) {
    suspend fun catalog(page: Int = 1) = api.catalog(page)
    suspend fun search(query: String) = api.search(query)
}
