package fr.estiam.traillog.data

import androidx.paging.PagingSource
import fr.estiam.traillog.model.Trail
import fr.estiam.traillog.model.TrailSortOrder
import kotlinx.coroutines.flow.Flow

interface TrailRepository {
    fun pagingSource(sort: TrailSortOrder, favoritesOnly: Boolean): PagingSource<Int, Trail>
    fun observeTrails(): Flow<List<Trail>>
    suspend fun getTrail(id: String): Trail?
    suspend fun save(trail: Trail)
    suspend fun delete(id: String)
    suspend fun seedIfNeeded()
}
