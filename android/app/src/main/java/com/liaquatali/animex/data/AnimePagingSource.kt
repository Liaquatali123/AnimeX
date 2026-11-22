package com.liaquatali.animex.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

class AnimePagingSource(
    private val api: CatalogApi,
    private val query: String? = null
) : PagingSource<Int, AnimeTitle>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeTitle> = try {
        val page = params.key ?: 1
        val response = if (query.isNullOrBlank()) api.catalog(page, 20) else api.search(query, page)
        LoadResult.Page(
            data = response.items,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (response.has_next) page + 1 else null
        )
    } catch (error: Exception) {
        LoadResult.Error(error)
    }

    override fun getRefreshKey(state: PagingState<Int, AnimeTitle>): Int? =
        state.anchorPosition?.let { position -> state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(position)?.nextKey?.minus(1) }
}
