package fr.estiam.traillog

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import fr.estiam.traillog.ui.MainActivity
import org.junit.Rule
import org.junit.runner.RunWith

/** M5 Lab 2: complete the Espresso tests using existing R.id values. */
@RunWith(AndroidJUnit4::class)
class TrailListEspressoTest {
    @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // TODO: Check R.id.trailList is displayed using Espresso onView().
    // TODO: Check the add button R.id.addButton opens the edit form.
    // TODO: Add a trail through the UI with a unique name, valid distance and elevation.
    // TODO: Use RecyclerViewActions.scrollTo() and a content-based assertion.
    // TODO: Allow Paging3/Room asynchronous work to finish deterministically.
    // TODO: Clean up created test data by ID or via a dedicated test setup.
}
