package fr.estiam.traillog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import fr.estiam.traillog.TrailLogApplication
import fr.estiam.traillog.databinding.ActivitySettingsBinding
import fr.estiam.traillog.model.DistanceUnit
import fr.estiam.traillog.model.TrailSortOrder
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var b: ActivitySettingsBinding
    private val prefs get() = (application as TrailLogApplication).settings
    private var loading = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsBinding.inflate(layoutInflater); setContentView(b.root)
        b.unitsToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked && !loading) lifecycleScope.launch {
                prefs.setUnit(if (checkedId == b.miButton.id) DistanceUnit.MI else DistanceUnit.KM)
            }
        }
        b.sortRadio.setOnCheckedChangeListener { _, id ->
            if (!loading) lifecycleScope.launch { prefs.setSort(if (id == b.sortDistance.id) TrailSortOrder.DISTANCE else TrailSortOrder.NAME) }
        }
        lifecycleScope.launch {
            prefs.settings.collect { settings ->
                loading = true
                b.unitsToggle.check(if (settings.unit == DistanceUnit.KM) b.kmButton.id else b.miButton.id)
                b.sortRadio.check(if (settings.sortOrder == TrailSortOrder.NAME) b.sortName.id else b.sortDistance.id)
                loading = false
                b.settingsStatus.text = "${settings.unit.name} · ${settings.sortOrder.name}"
            }
        }
    }
}
