package fr.estiam.traillog.data

import fr.estiam.traillog.model.UserSettings
import fr.estiam.traillog.model.DistanceUnit
import fr.estiam.traillog.model.TrailSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface PreferencesRepository {
    val settings: Flow<UserSettings>
    suspend fun setUnit(unit: DistanceUnit)
    suspend fun setSort(sort: TrailSortOrder)
}

/** Before M3 settings exist in memory only. */
class MemoryPreferencesRepository : PreferencesRepository {
    private val state = MutableStateFlow(UserSettings())
    override val settings: Flow<UserSettings> = state
    override suspend fun setUnit(unit: DistanceUnit) { state.value = state.value.copy(unit = unit) }
    override suspend fun setSort(sort: TrailSortOrder) { state.value = state.value.copy(sortOrder = sort) }
}
