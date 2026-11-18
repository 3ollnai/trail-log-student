package fr.estiam.traillog.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrailEntity::class], version = 1, exportSchema = false)
abstract class TrailDatabase : RoomDatabase() {
    abstract fun trailDao(): TrailDao
    companion object {
        @Volatile private var instance: TrailDatabase? = null
        fun get(context: Context): TrailDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, TrailDatabase::class.java,
                "traillog.db").build().also { instance = it }
        }
    }
}
