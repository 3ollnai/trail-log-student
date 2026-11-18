package fr.estiam.traillog.data.local

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrailDao {
    @Query("SELECT * FROM trails ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun pagingByName(): PagingSource<Int, TrailEntity>

    @Query("SELECT * FROM trails ORDER BY distanceKm DESC, name COLLATE NOCASE ASC, id ASC")
    fun pagingByDistance(): PagingSource<Int, TrailEntity>

    @Query("SELECT * FROM trails WHERE isFavorite = 1 ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun pagingFavoriteByName(): PagingSource<Int, TrailEntity>

    @Query("SELECT * FROM trails WHERE isFavorite = 1 ORDER BY distanceKm DESC, name COLLATE NOCASE ASC, id ASC")
    fun pagingFavoriteByDistance(): PagingSource<Int, TrailEntity>

    @Query("SELECT * FROM trails ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<TrailEntity>>

    @Query("SELECT * FROM trails WHERE id = :id LIMIT 1")
    suspend fun byId(id: String): TrailEntity?

    @Query("SELECT COUNT(*) FROM trails")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(trail: TrailEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(trails: List<TrailEntity>)

    @Query("DELETE FROM trails WHERE id = :id")
    suspend fun deleteById(id: String)
}
