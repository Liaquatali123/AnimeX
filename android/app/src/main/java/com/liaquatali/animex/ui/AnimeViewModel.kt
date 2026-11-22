package com.liaquatali.animex.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.liaquatali.animex.data.AnimeDatabase
import com.liaquatali.animex.data.AnimeRepository
import com.liaquatali.animex.data.FavoriteEntity
import com.liaquatali.animex.data.AnimeTitle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class AnimeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AnimeRepository()
    private val favoritesDao = AnimeDatabase.get(application).favoriteDao()
    private val _titles = MutableStateFlow<List<AnimeTitle>>(emptyList())
    val titles: StateFlow<List<AnimeTitle>> = _titles.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    val favorites = favoritesDao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init { loadCatalog() }

    fun loadCatalog() = viewModelScope.launch {
        _loading.value = true
        _error.value = null
        try { _titles.value = repository.catalog().items }
        catch (e: Exception) { _error.value = "Can't reach the catalog. Check your API URL and connection." }
        finally { _loading.value = false }
    }

    fun search(query: String) = viewModelScope.launch {
        if (query.isBlank()) { loadCatalog(); return@launch }
        _loading.value = true
        _error.value = null
        try { _titles.value = repository.search(query.trim()).items }
        catch (e: Exception) { _error.value = "Search unavailable. Check your connection." }
        finally { _loading.value = false }
    }

    fun toggleFavorite(title: AnimeTitle) = viewModelScope.launch {
        if (favoritesDao.contains(title.id)) favoritesDao.remove(title.id)
        else favoritesDao.add(FavoriteEntity(title.id, title.title, title.poster_url, title.overview))
    }
}
