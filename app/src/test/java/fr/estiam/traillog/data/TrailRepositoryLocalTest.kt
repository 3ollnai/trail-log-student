package fr.estiam.traillog.data

import androidx.paging.PagingSource
import fr.estiam.traillog.model.Trail
import fr.estiam.traillog.model.TrailSortOrder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/** M5 Lab 1: exercise the real MemoryTrailRepository without an Android device.
 * The repository already implements the TrailRepository contract. */
class TrailRepositoryLocalTest {
    // TODO: Create deterministic fixtures with known IDs, distances and names.
    // TODO: Test save + getTrail and update by stable String ID.
    // TODO: Test delete and observeTrails using Flow.first().
    // TODO: Test NAME sorting through the PagingSource result.
    // TODO: Test DISTANCE sorting and an empty-result edge case.
    // Hint: PagingSource.LoadParams.Refresh(key=null, loadSize=100, placeholdersEnabled=false)
    // Hint: runBlocking { ... } for suspend repository methods.
}
