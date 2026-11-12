package fr.estiam.traillog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import fr.estiam.traillog.data.TrailRepository
import fr.estiam.traillog.data.PreferencesRepository
import fr.estiam.traillog.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class TrailListViewModel(
    private val repo: TrailRepository,
    prefs: PreferencesRepository
) : ViewModel() {
    private val favoritesOnly = MutableStateFlow(false)
    private val localSort = MutableStateFlow<TrailSortOrder?>(null)
    val settings = prefs.settings.stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())
    val favoritesMode = favoritesOnly.asStateFlow()
    fun showFavorites(only: Boolean) { favoritesOnly.value = only }
    fun sortBy(value: TrailSortOrder) { localSort.value = value }

    /** M3 lab: can be observed as lifecycle-aware LiveData for reactive CRUD. */
    val allTrailsLiveData = repo.observeTrails().asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val trails: Flow<PagingData<Trail>> = combine(favoritesOnly, settings, localSort) { only, prefsValue, override ->
        Pair(only, override ?: prefsValue.sortOrder)
    }.distinctUntilChanged().flatMapLatest { (only, sort) ->
        Pager(PagingConfig(pageSize = 12, enablePlaceholders = false)) {
            repo.pagingSource(sort, only)
        }.flow
    }.cachedIn(viewModelScope)

    class Factory(private val repo: TrailRepository, private val prefs: PreferencesRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = TrailListViewModel(repo, prefs) as T
    }
}
