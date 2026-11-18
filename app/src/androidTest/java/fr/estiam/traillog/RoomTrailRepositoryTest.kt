package fr.estiam.traillog

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fr.estiam.traillog.data.local.TrailDatabase
import fr.estiam.traillog.data.local.RoomTrailRepository
import fr.estiam.traillog.model.Trail
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomTrailRepositoryTest {
    @Test fun crudPersists() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, TrailDatabase::class.java).build()
        try {
            val repo = RoomTrailRepository(context, db.trailDao())
            val trail = Trail("example", "Alpine", "Alpes", 10.0, 200)
            repo.save(trail)
            assertEquals("Alpine", repo.getTrail("example")?.name)
            repo.save(trail.copy(name = "Updated"))
            assertEquals("Updated", repo.getTrail("example")?.name)
            repo.delete("example")
            assertNull(repo.getTrail("example"))
        } finally { db.close() }
    }
}
