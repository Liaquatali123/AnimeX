package com.liaquatali.animex.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

class AnimePagingSource(
    private val api: CatalogApi,
    private val cacheDao: CatalogCacheDao,
    private val query: String? = null
) : PagingSource<Int, AnimeTitle>() {
    private val cacheKey = query?.let { "search:${it.trim().lowercase()}" } ?: "catalog"

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeTitle> {
        val page = params.key ?: 1
        return try {
            val response = if (query.isNullOrBlank()) api.catalog(page, 20) else api.search(query, page)
            cacheDao.insertAll(response.items.map { CatalogCacheEntity.from(cacheKey, page, it) })
            LoadResult.Page(
                data = response.items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.has_next) page + 1 else null
            )
        } catch (error: Exception) {
            val cached = cacheDao.getPage(cacheKey, page).map { it.toAnimeTitle() }
            if (cached.isNotEmpty()) {
                LoadResult.Page(
                    data = cached,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (cached.size >= 20) page + 1 else null
                )
            } else LoadResult.Error(error)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, AnimeTitle>): Int? =
        state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
}
