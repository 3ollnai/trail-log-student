package fr.estiam.traillog.data

import androidx.paging.PagingSource
import fr.estiam.traillog.model.Difficulty
import fr.estiam.traillog.model.Trail
import fr.estiam.traillog.model.TrailSortOrder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/** M5 Lab 1: exercise the real MemoryTrailRepository without an Android device.
 * The repository already implements the TrailRepository contract. */
class TrailRepositoryLocalTest {
    @Test
    fun saveAndGetTrail_updateKeepsStableStringId() = runBlocking {
        // Arrange
        val repository = MemoryTrailRepository()
        val original = trail("pine-1", "Pine Loop", 3.2)

        // Act
        repository.save(original)
        val firstRead = repository.getTrail("pine-1")
        val updated = original.copy(name = "Pine Loop Updated", distanceKm = 4.1)
        repository.save(updated)
        val secondRead = repository.getTrail("pine-1")
        val matches = repository.observeTrails().first().filter { it.id == "pine-1" }

        // Assert
        assertEquals(original, firstRead)
        assertEquals(updated, secondRead)
        assertEquals("updating a stable ID must not create a duplicate", 1, matches.size)
    }

    @Test
    fun deleteRemovesTrailFromLookupAndObservedList() = runBlocking {
        // Arrange
        val repository = MemoryTrailRepository()
        repository.save(trail("creek-3", "Creekside", 1.0))

        // Act
        repository.delete("creek-3")

        // Assert
        assertNull(repository.getTrail("creek-3"))
        assertFalse(repository.observeTrails().first().any { it.id == "creek-3" })
    }

    @Test
    fun nameSortOrdersKnownTrailsAlphabetically() = runBlocking {
        // Arrange
        val repository = MemoryTrailRepository()
        val fixtures = listOf(
            trail("pine-1", "Pine Loop", 3.2),
            trail("ridge-2", "Ridge Run", 7.5),
            trail("creek-3", "Creekside", 1.0)
        )
        fixtures.forEach { repository.save(it) }
        val fixtureIds = fixtures.map { it.id }.toSet()

        // Act
        val orderedIds = loadPage(repository, TrailSortOrder.NAME)
            .filter { it.id in fixtureIds }
            .map { it.id }

        // Assert
        assertEquals(listOf("creek-3", "pine-1", "ridge-2"), orderedIds)
    }

    @Test
    fun distanceSortOrdersKnownTrailsFromLongestToShortest() = runBlocking {
        // Arrange
        val repository = MemoryTrailRepository()
        val fixtures = listOf(
            trail("pine-1", "Pine Loop", 3.2),
            trail("ridge-2", "Ridge Run", 7.5),
            trail("creek-3", "Creekside", 1.0)
        )
        fixtures.forEach { repository.save(it) }
        val fixtureIds = fixtures.map { it.id }.toSet()

        // Act
        val orderedIds = loadPage(repository, TrailSortOrder.DISTANCE)
            .filter { it.id in fixtureIds }
            .map { it.id }

        // Assert
        assertEquals(listOf("ridge-2", "pine-1", "creek-3"), orderedIds)
    }

    @Test
    fun favoriteOnlySortExcludesNonFavoriteFixtures() = runBlocking {
        // Arrange
        val repository = MemoryTrailRepository()
        val fixtures = listOf(
            trail("pine-1", "Pine Loop", 3.2),
            trail("ridge-2", "Ridge Run", 7.5),
            trail("creek-3", "Creekside", 1.0)
        )
        fixtures.forEach { repository.save(it) }
        val fixtureIds = fixtures.map { it.id }.toSet()

        // Act
        val favoriteFixtureIds = loadPage(repository, TrailSortOrder.NAME, favoritesOnly = true)
            .filter { it.id in fixtureIds }
            .map { it.id }

        // Assert
        assertTrue(favoriteFixtureIds.isEmpty())
    }

    @Test
    fun unknownStringIdReturnsNull() = runBlocking {
        // Arrange
        val repository = MemoryTrailRepository()

        // Act
        val result = repository.getTrail("missing-trail")

        // Assert
        assertNull(result)
    }

    private fun trail(id: String, name: String, distanceKm: Double) = Trail(
        id = id,
        name = name,
        region = "Alpes",
        distanceKm = distanceKm,
        elevationGainM = 200,
        difficulty = Difficulty.MODERATE,
        notes = "Fixture",
        isFavorite = false,
        updatedAt = 1_700_000_000_000L
    )

    private suspend fun loadPage(
        repository: MemoryTrailRepository,
        sort: TrailSortOrder,
        favoritesOnly: Boolean = false
    ): List<Trail> {
        val source = repository.pagingSource(sort, favoritesOnly)
        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 100, placeholdersEnabled = false)
        )
        assertTrue("expected a page result", result is PagingSource.LoadResult.Page)
        return (result as PagingSource.LoadResult.Page).data
    }
}

