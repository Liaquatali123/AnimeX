package com.liaquatali.animex.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.liaquatali.animex.data.AnimeDatabase
import com.liaquatali.animex.data.AnimePagingSource
import com.liaquatali.animex.data.AnimeTitle
import com.liaquatali.animex.data.FavoriteEntity
import com.liaquatali.animex.data.NetworkModule
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class AnimeViewModel(application: Application) : AndroidViewModel(application) {
    private val favoritesDao = AnimeDatabase.get(application).favoriteDao()
    private val api = NetworkModule.api
    private val historyDao = AnimeDatabase.get(application).watchHistoryDao()
    val catalog = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
        AnimePagingSource(api)
    }.flow.cachedIn(viewModelScope)

    private val _searchResults = MutableStateFlow<PagingData<AnimeTitle>>(PagingData.empty())
    val searchResults: StateFlow<PagingData<AnimeTitle>> = _searchResults.asStateFlow()
    private var searchJob: Job? = null
    val favorites = favoritesDao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val history = historyDao.observeRecent().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun search(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = PagingData.empty()
            return
        }
        searchJob = viewModelScope.launch {
            Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
                AnimePagingSource(api, query.trim())
            }.flow.collect { _searchResults.value = it }
        }
    }

    fun toggleFavorite(title: AnimeTitle) = viewModelScope.launch {
        if (favoritesDao.contains(title.id)) favoritesDao.remove(title.id)
        else favoritesDao.add(FavoriteEntity(title.id, title.title, title.poster_url, title.overview))
    }
}
