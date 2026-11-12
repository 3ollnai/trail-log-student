package fr.estiam.traillog.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import fr.estiam.traillog.R
import fr.estiam.traillog.TrailLogApplication
import fr.estiam.traillog.databinding.ActivityMainBinding
import fr.estiam.traillog.model.TrailSortOrder
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private val app get() = application as TrailLogApplication
    private val vm: TrailListViewModel by viewModels { TrailListViewModel.Factory(app.trails, app.settings) }
    private val adapter = TrailPagingAdapter { trail ->
        startActivity(Intent(this, TrailDetailActivity::class.java).putExtra(TrailDetailActivity.EXTRA_ID, trail.id))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater); setContentView(b.root)
        setSupportActionBar(b.toolbar)
        val toggle = ActionBarDrawerToggle(this, b.drawerLayout, b.toolbar, R.string.all_trails, R.string.close)
        b.drawerLayout.addDrawerListener(toggle); toggle.syncState()
        b.trailList.layoutManager = LinearLayoutManager(this)
        b.trailList.adapter = adapter
        b.retryButton.setOnClickListener { adapter.retry() }
        b.addButton.setOnClickListener { startActivity(Intent(this, EditTrailActivity::class.java)) }
        b.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_all -> { vm.showFavorites(false); supportActionBar?.title = getString(R.string.all_trails) }
                R.id.drawer_favorites -> { vm.showFavorites(true); supportActionBar?.title = getString(R.string.favorites) }
                R.id.drawer_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            }
            item.isChecked = true; b.drawerLayout.closeDrawer(GravityCompat.START); true
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { app.trails.seedIfNeeded() }
                launch { vm.trails.collect { adapter.submitData(it) } }
                launch { vm.settings.collect { adapter.unit = it.unit } }
                launch {
                    adapter.loadStateFlow.collect { state ->
                        b.progress.visibility = if (state.refresh is LoadState.Loading) View.VISIBLE else View.GONE
                        b.errorPanel.visibility = if (state.refresh is LoadState.Error) View.VISIBLE else View.GONE
                        b.emptyText.visibility = if (state.refresh is LoadState.NotLoading && adapter.itemCount == 0) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu); return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_sort_name -> { vm.sortBy(TrailSortOrder.NAME); true }
        R.id.action_sort_distance -> { vm.sortBy(TrailSortOrder.DISTANCE); true }
        R.id.action_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
        else -> super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        if (b.drawerLayout.isDrawerOpen(GravityCompat.START)) b.drawerLayout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
