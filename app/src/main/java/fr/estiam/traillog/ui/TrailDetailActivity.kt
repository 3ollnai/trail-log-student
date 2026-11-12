package fr.estiam.traillog.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import fr.estiam.traillog.R
import fr.estiam.traillog.TrailLogApplication
import fr.estiam.traillog.databinding.ActivityTrailDetailBinding
import fr.estiam.traillog.model.*
import fr.estiam.traillog.worker.TrailReminderWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TrailDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityTrailDetailBinding
    private val app get() = application as TrailLogApplication
    private var trail: Trail? = null
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) trail?.let { TrailReminderWorker.notifyNow(this, it) }
        else Snackbar.make(b.root, R.string.permission_rationale, Snackbar.LENGTH_LONG).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTrailDetailBinding.inflate(layoutInflater); setContentView(b.root)
        val id = intent.getStringExtra(EXTRA_ID) ?: run { finish(); return }
        b.editButton.setOnClickListener { startActivity(Intent(this, EditTrailActivity::class.java).putExtra(EXTRA_ID, id)) }
        b.favoriteButton.setOnClickListener {
            val current = trail ?: return@setOnClickListener
            lifecycleScope.launch { app.trails.save(current.copy(isFavorite = !current.isFavorite, updatedAt = System.currentTimeMillis())); reload(id) }
        }
        b.deleteButton.setOnClickListener { val previous = trail ?: return@setOnClickListener
            lifecycleScope.launch {
                app.trails.delete(previous.id)
                Snackbar.make(b.root, R.string.deleted, Snackbar.LENGTH_LONG).setAction(R.string.undo) {
                    lifecycleScope.launch { app.trails.save(previous); reload(id) }
                }.show()
                // Keep this screen alive briefly so Undo remains available.
                b.deleteButton.isEnabled = false
            }
        }
        b.shareButton.setOnClickListener {
            val value = trail ?: return@setOnClickListener
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, "${value.name} (${value.distanceKm} km), ${value.region}")
            }, getString(R.string.share_title)))
        }
        b.notifyButton.setOnClickListener {
            val current = trail ?: return@setOnClickListener
            if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else TrailReminderWorker.notifyNow(this, current)
        }
        lifecycleScope.launch { reload(id) }
    }
    override fun onResume() { super.onResume(); intent.getStringExtra(EXTRA_ID)?.let { lifecycleScope.launch { reload(it) } } }
    private suspend fun reload(id: String) {
        trail = app.trails.getTrail(id)
        val value = trail ?: run { b.titleText.text = getString(R.string.title_missing); return }
        val settings = app.settings.settings.first()
        b.titleText.text = value.name
        b.regionText.text = value.region
        b.distanceText.text = TrailFormatter.distance(value.distanceKm, settings.unit)
        b.elevationText.text = "↑ ${value.elevationGainM} m"
        b.notesText.text = value.notes.ifBlank { getString(R.string.no_notes) }
        b.favoriteButton.text = if (value.isFavorite) "★" else "☆"
        b.elevationCurve.setSamples(listOf(100f, 130f, 110f, 170f, 140f, 220f, 180f, 140f))
        b.elevationCurve.contentDescription = "${getString(R.string.profile)}: 100–220 m"
        b.deleteButton.isEnabled = true
    }
    companion object { const val EXTRA_ID = "trail_id" }
}
