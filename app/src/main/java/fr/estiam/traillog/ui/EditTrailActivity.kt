package fr.estiam.traillog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import fr.estiam.traillog.R
import fr.estiam.traillog.TrailLogApplication
import fr.estiam.traillog.databinding.ActivityEditTrailBinding
import fr.estiam.traillog.model.Trail
import java.util.UUID
import kotlinx.coroutines.launch

class EditTrailActivity : AppCompatActivity() {
    private lateinit var b: ActivityEditTrailBinding
    private val app get() = application as TrailLogApplication
    private var original: Trail? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditTrailBinding.inflate(layoutInflater); setContentView(b.root)
        val id = intent.getStringExtra(TrailDetailActivity.EXTRA_ID)
        if (id != null) lifecycleScope.launch { app.trails.getTrail(id)?.let { original = it; show(it) } }
        b.saveButton.setOnClickListener {
            val name = b.nameInput.text.toString().trim()
            val distance = b.distanceInput.text.toString().toDoubleOrNull()
            val elevation = b.elevationInput.text.toString().toIntOrNull()
            if (name.isBlank() || distance == null || !distance.isFinite() || distance < 0 || elevation == null || elevation < 0) {
                Snackbar.make(b.root, R.string.invalid_fields, Snackbar.LENGTH_LONG).show(); return@setOnClickListener
            }
            val value = (original ?: Trail(UUID.randomUUID().toString(), name, "", distance, elevation)).copy(
                name = name, region = b.regionInput.text.toString().trim(), distanceKm = distance,
                elevationGainM = elevation, notes = b.notesInput.text.toString(), updatedAt = System.currentTimeMillis())
            lifecycleScope.launch { app.trails.save(value); setResult(RESULT_OK); finish() }
        }
    }
    private fun show(value: Trail) {
        b.nameInput.setText(value.name); b.regionInput.setText(value.region)
        b.distanceInput.setText(value.distanceKm.toString()); b.elevationInput.setText(value.elevationGainM.toString())
        b.notesInput.setText(value.notes)
    }
}
