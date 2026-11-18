package fr.estiam.traillog.data.local

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import fr.estiam.traillog.data.TrailRepository
import fr.estiam.traillog.model.Trail
import fr.estiam.traillog.model.TrailSortOrder
import org.json.JSONArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** M3 correction: all writes go through the repository, Room is the authority. */
class RoomTrailRepository(private val context: Context, private val dao: TrailDao) : TrailRepository {
    private val seedLock = Mutex()
    override fun observeTrails(): Flow<List<Trail>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }
    override fun pagingSource(sort: TrailSortOrder, favoritesOnly: Boolean): PagingSource<Int, Trail> =
        EntityPagingSource(when {
            favoritesOnly && sort == TrailSortOrder.NAME -> dao.pagingFavoriteByName()
            favoritesOnly && sort == TrailSortOrder.DISTANCE -> dao.pagingFavoriteByDistance()
            sort == TrailSortOrder.DISTANCE -> dao.pagingByDistance()
            else -> dao.pagingByName()
        })
    override suspend fun getTrail(id: String): Trail? = dao.byId(id)?.toDomain()
    override suspend fun save(trail: Trail) { require(trail.isValid()); dao.upsert(trail.toEntity()) }
    override suspend fun delete(id: String) = dao.deleteById(id)
    override suspend fun seedIfNeeded() {
        seedLock.withLock {
            // A durable flag avoids reimporting after the user deletes the last trail.
            val flags = context.getSharedPreferences("traillog_seed", Context.MODE_PRIVATE)
            if (flags.getBoolean("seed_complete", false)) return@withLock
            if (dao.count() > 0) {
                flags.edit().putBoolean("seed_complete", true).apply()
                return@withLock
            }
            val text = context.assets.open("trails.json").bufferedReader().use { it.readText() }
            val array = JSONArray(text)
            val records = (0 until array.length()).mapNotNull { i ->
                val obj = array.getJSONObject(i)
                val id = obj.optString("id").trim()
                val name = obj.optString("name").trim()
                val distance = obj.optDouble("distanceKm", -1.0)
                val elevation = obj.optInt("elevationGainM", -1)
                if (id.isBlank() || name.isBlank() || !distance.isFinite() || distance < 0 || elevation < 0) null
                else Trail(id, name, obj.optString("region"), distance, elevation,
                    fr.estiam.traillog.model.Difficulty.entries.firstOrNull { it.name == obj.optString("difficulty") }
                        ?: fr.estiam.traillog.model.Difficulty.MODERATE,
                    obj.optString("notes"), obj.optBoolean("isFavorite", false),
                    1_700_000_000_000L + i).toEntity()
            }
            dao.insertAll(records)
            flags.edit().putBoolean("seed_complete", true).apply()
        }
    }
}

/** Keep the same domain-facing PagingSource contract across M2 and M3. */
private class EntityPagingSource(private val delegate: PagingSource<Int, TrailEntity>) : PagingSource<Int, Trail>() {
    init { delegate.registerInvalidatedCallback { invalidate() } }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Trail> = when(val result = delegate.load(params)) {
        is LoadResult.Page -> LoadResult.Page(result.data.map { it.toDomain() }, result.prevKey, result.nextKey,
            result.itemsBefore, result.itemsAfter)
        is LoadResult.Error -> LoadResult.Error(result.throwable)
        is LoadResult.Invalid -> LoadResult.Invalid()
    }
    override fun getRefreshKey(state: PagingState<Int, Trail>): Int? = state.anchorPosition
}
