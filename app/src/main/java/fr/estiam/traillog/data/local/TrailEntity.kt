package fr.estiam.traillog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.estiam.traillog.model.Difficulty
import fr.estiam.traillog.model.Trail

@Entity(tableName = "trails")
data class TrailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val region: String,
    val distanceKm: Double,
    val elevationGainM: Int,
    val difficulty: String,
    val notes: String,
    val isFavorite: Boolean,
    val updatedAt: Long
) {
    fun toDomain(): Trail = Trail(id, name, region, distanceKm, elevationGainM,
        Difficulty.entries.firstOrNull { it.name == difficulty } ?: Difficulty.MODERATE,
        notes, isFavorite, updatedAt)
}
fun Trail.toEntity() = TrailEntity(id, name, region, distanceKm, elevationGainM,
    difficulty.name, notes, isFavorite, updatedAt)
