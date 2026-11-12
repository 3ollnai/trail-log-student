package fr.estiam.traillog.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fr.estiam.traillog.model.DistanceUnit
import fr.estiam.traillog.model.TrailSortOrder
import fr.estiam.traillog.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.trailSettingsDataStore by preferencesDataStore(
    name = "traillog_settings"
)

class DataStorePreferencesRepository(
    private val context: Context
) : PreferencesRepository {
    private val unitKey = stringPreferencesKey("pref_distance_units")
    private val sortKey = stringPreferencesKey("pref_default_sort")

    override val settings: Flow<UserSettings> =
        context.applicationContext.trailSettingsDataStore.data
            .catch { error ->
                if (error is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw error
                }
            }
            .map { preferences ->
                UserSettings(
                    unit = DistanceUnit.entries.firstOrNull {
                        it.name == preferences[unitKey]
                    } ?: DistanceUnit.KM,
                    sortOrder = TrailSortOrder.entries.firstOrNull {
                        it.name == preferences[sortKey]
                    } ?: TrailSortOrder.NAME
                )
            }

    override suspend fun setUnit(unit: DistanceUnit) {
        context.applicationContext.trailSettingsDataStore.edit { preferences ->
            preferences[unitKey] = unit.name
        }
    }

    override suspend fun setSort(sort: TrailSortOrder) {
        context.applicationContext.trailSettingsDataStore.edit { preferences ->
            preferences[sortKey] = sort.name
        }
    }
}

