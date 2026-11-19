package fr.estiam.traillog

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.registerIdlingResources
import androidx.test.espresso.Espresso.unregisterIdlingResources
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fr.estiam.traillog.data.local.TrailDatabase
import fr.estiam.traillog.model.Trail
import fr.estiam.traillog.ui.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class TrailListEspressoTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private var createdTrailName: String? = null

    @Test
    fun catalogueRecyclerView_isDisplayed() {
        onView(withId(R.id.trailList)).check(matches(isDisplayed()))
    }

    @Test
    fun addButton_opensEditScreen() {
        onView(withId(R.id.addButton)).perform(click())

        onView(withId(R.id.nameInput)).check(matches(isDisplayed()))
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()))
    }

    @Test
    fun addingTrail_displaysCreatedItemInRecyclerView() {
        // Arrange: make the row appear near the start regardless of the saved sort preference.
        val name = "AAA M5 UI ${UUID.randomUUID()}"
        createdTrailName = name
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withId(R.id.action_sort_name)).perform(click())

        // Act: create a uniquely named trail through the real form.
        onView(withId(R.id.addButton)).perform(click())
        onView(withId(R.id.nameInput)).perform(replaceText(name), closeSoftKeyboard())
        onView(withId(R.id.distanceInput)).perform(replaceText("8.5"), closeSoftKeyboard())
        onView(withId(R.id.elevationInput)).perform(replaceText("750"), closeSoftKeyboard())
        onView(withId(R.id.saveButton)).perform(click())

        // Assert the catalogue returns and eventually contains this exact trail.
        onView(withId(R.id.trailList)).check(matches(isDisplayed()))
        lateinit var trailList: RecyclerView
        activityRule.scenario.onActivity { activity ->
            trailList = activity.findViewById(R.id.trailList)
        }
        val idler = CatalogueItemIdler(trailList, name)
        registerIdlingResources(idler)
        try {
            onView(withId(R.id.trailList)).check(matches(isDisplayed()))
            onView(withId(R.id.trailList)).perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(name)))
            )
            onView(allOf(withText(name), isDisplayed())).check(matches(isDisplayed()))
        } finally {
            unregisterIdlingResources(idler)
            idler.unregister()
        }
    }

    @After
    fun removeOnlyTheTrailCreatedByThisTest() = runBlocking {
        val name = createdTrailName ?: return@runBlocking
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dao = TrailDatabase.get(context).trailDao()
        val entity = dao.observeAll().first().firstOrNull { it.name == name }
        if (entity != null) dao.deleteById(entity.id)
        createdTrailName = null
    }

    /** Waits for the PagingDataAdapter snapshot without sleeping or blocking the main thread. */
    private class CatalogueItemIdler(
        private val trailList: RecyclerView,
        private val expectedName: String
    ) : IdlingResource {
        @Volatile
        private var callback: IdlingResource.ResourceCallback? = null
        private var adapter: PagingDataAdapter<*, *>? = null
        private var loadStateListener: ((androidx.paging.CombinedLoadStates) -> Unit)? = null

        override fun getName(): String = "Catalogue contains $expectedName"

        override fun isIdleNow(): Boolean {
            val present = adapter?.snapshot()?.items?.any { item ->
                item is Trail && item.name == expectedName
            } == true
            return present
        }

        override fun registerIdleTransitionCallback(cb: IdlingResource.ResourceCallback) {
            callback = cb
            adapter = trailList.adapter as? PagingDataAdapter<*, *>
            loadStateListener = {
                if (isIdleNow()) cb.onTransitionToIdle()
            }
            loadStateListener?.let { listener -> adapter?.addLoadStateListener(listener) }
            if (isIdleNow()) cb.onTransitionToIdle()
        }

        fun unregister() {
            loadStateListener?.let { listener -> adapter?.removeLoadStateListener(listener) }
            loadStateListener = null
            adapter = null
            callback = null
        }
    }
}

