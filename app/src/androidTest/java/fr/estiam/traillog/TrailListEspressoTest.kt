package fr.estiam.traillog

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.estiam.traillog.ui.MainActivity
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** M5 Lab 2: implement Espresso UI tests using the existing TrailLog view IDs. */
@RunWith(AndroidJUnit4::class)
class TrailListEspressoTest {
    @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // A skipped placeholder keeps JUnit 4 discovery valid in the unfinished starter.
    // Replace this with your own @Test methods during the lab.
    @Ignore("Student exercise: implement the catalogue Espresso assertions")
    @Test
    fun implementCatalogueTests() {
        // TODO: Check R.id.trailList is displayed with Espresso.
    }

    // TODO: Check that R.id.addButton opens the edit form.
    // TODO: Enter a valid trail through the UI and save it.
    // TODO: Scroll to the created RecyclerView item by content and assert its text.
    // TODO: Handle asynchronous Room/Paging3 updates without Thread.sleep().
    // TODO: Avoid stale data / clean up only the records created by your tests.
}
