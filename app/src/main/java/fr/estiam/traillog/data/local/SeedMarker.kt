package fr.estiam.traillog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Durable proof that the bundled initial trails have already been imported. */
@Entity(tableName = "seed_markers")
data class SeedMarker(
    @PrimaryKey val key: String
)