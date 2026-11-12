package fr.estiam.traillog.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import fr.estiam.traillog.model.Difficulty
import fr.estiam.traillog.model.Trail
import fr.estiam.traillog.model.TrailSortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** M1/M2 demonstration source. Data intentionally resets when the process dies. */
class MemoryTrailRepository : TrailRepository {
    private val lock = Mutex()
    private val initial = (1..45).map { i ->
        val regions = listOf("Alpes", "Pyrénées", "Vosges", "Jura", "Auvergne")
        Trail("sample_$i", "Parcours ${"%02d".format(i)}", regions[i % regions.size],
            2.0 + (i % 16) * 0.9, (i % 10) * 75,
            Difficulty.entries[i % Difficulty.entries.size],
            "Une randonnée de démonstration, prête à explorer.", i % 6 == 0,
            1_700_000_000_000L + i * 1000L)
    }
    private val items = MutableStateFlow(initial)
    private val sources = mutableListOf<PagingSource<Int, Trail>>()

    override fun observeTrails(): Flow<List<Trail>> = items
    override fun pagingSource(sort: TrailSortOrder, favoritesOnly: Boolean): PagingSource<Int, Trail> {
        val all = items.value.let { if (favoritesOnly) it.filter { trail -> trail.isFavorite } else it }
        val sorted = when(sort) {
            TrailSortOrder.NAME -> all.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            TrailSortOrder.DISTANCE -> all.sortedWith(compareByDescending<Trail> { it.distanceKm }.thenBy { it.name })
        }
        return MemoryPagingSource(sorted).also { sources.add(it) }
    }
    override suspend fun getTrail(id: String): Trail? = items.value.firstOrNull { it.id == id }
    override suspend fun save(trail: Trail) {
        require(trail.isValid())
        lock.withLock { items.value = items.value.filterNot { it.id == trail.id } + trail; invalidate() }
    }
    override suspend fun delete(id: String) { lock.withLock { items.value = items.value.filterNot { it.id == id }; invalidate() } }
    override suspend fun seedIfNeeded() = Unit
    private fun invalidate() { sources.toList().forEach { it.invalidate() }; sources.clear() }
}

class MemoryPagingSource(private val data: List<Trail>) : PagingSource<Int, Trail>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Trail> {
        val offset = (params.key ?: 0).coerceAtLeast(0)
        val end = (offset + params.loadSize).coerceAtMost(data.size)
        return LoadResult.Page(data = if (offset >= data.size) emptyList() else data.subList(offset, end),
            prevKey = if (offset == 0) null else (offset - params.loadSize).coerceAtLeast(0),
            nextKey = if (end >= data.size) null else end)
    }
    override fun getRefreshKey(state: PagingState<Int, Trail>): Int? = state.anchorPosition
}
