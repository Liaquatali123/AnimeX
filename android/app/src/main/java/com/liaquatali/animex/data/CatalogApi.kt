package com.liaquatali.animex.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApi {
    @GET("v1/catalog")
    suspend fun catalog(@Query("page") page: Int, @Query("limit") limit: Int = 20): CatalogResponse

    @GET("v1/search")
    suspend fun search(@Query("q") query: String, @Query("page") page: Int = 1): CatalogResponse
}
