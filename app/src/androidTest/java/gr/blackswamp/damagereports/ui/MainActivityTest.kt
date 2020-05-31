package gr.blackswamp.damagereports.ui

import android.content.Context
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.fragments.ReportListFragment
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun main_activity_loads() {
        onView(withId(R.id.base)).check(matches(isDisplayed()))
    }

    @Test
    fun toolbar_is_showing() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun container_is_showing() {
        onView(withId(R.id.container)).check(matches(isDisplayed()))
    }

    @Test
    fun progress_is_hidden() {
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun title_is_correct() {
        onView(withId(R.id.toolbar)).check { view, _ ->
            val tb = view as Toolbar
            assertEquals(context.getString(R.string.damage_reports), tb.title.toString())
        }
    }

    @Test
    fun base_container_has_the_list_of_reports_fragment() {
        onView(withId(R.id.container)).check { view, _ ->
            val destination = view.findNavController().currentDestination as FragmentNavigator.Destination
            assertEquals(destination.className, ReportListFragment::class.java.name)
        }
        onView(withId(R.id.report_list)).check(matches(isDisplayed()))
    }
}