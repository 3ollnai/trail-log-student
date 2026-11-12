package fr.estiam.traillog.model

/** Shared domain model for the list, detail, edit and persistence exercises. */
data class Trail(
    val id: String,
    val name: String,
    val region: String,
    val distanceKm: Double,
    val elevationGainM: Int,
    val difficulty: Difficulty = Difficulty.MODERATE,
    val notes: String = "",
    val isFavorite: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isValid(): Boolean = id.isNotBlank() && name.isNotBlank() &&
        distanceKm.isFinite() && distanceKm >= 0.0 && elevationGainM >= 0
}

enum class Difficulty { EASY, MODERATE, HARD }
