package fr.estiam.traillog

import android.app.Application
import fr.estiam.traillog.data.PreferencesRepository
import fr.estiam.traillog.data.TrailRepository
import fr.estiam.traillog.data.DataStorePreferencesRepository
import fr.estiam.traillog.data.local.RoomTrailRepository
import fr.estiam.traillog.data.local.TrailDatabase
import fr.estiam.traillog.worker.TrailReminderWorker

class TrailLogApplication : Application() {
    val trails: TrailRepository by lazy {
        RoomTrailRepository(this, TrailDatabase.get(this).trailDao())
    }
    val settings: PreferencesRepository by lazy { DataStorePreferencesRepository(this) }
    override fun onCreate() {
        super.onCreate()
        TrailReminderWorker.ensureChannel(this)
    }
}
