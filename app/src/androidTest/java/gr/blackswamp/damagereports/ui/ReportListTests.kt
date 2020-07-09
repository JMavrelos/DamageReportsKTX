package gr.blackswamp.damagereports.ui

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.TestData
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ReportListTests {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        TestData.initialize(ApplicationProvider.getApplicationContext())
        activity.scenario.moveToState(Lifecycle.State.RESUMED)
    }


    @Test
    fun pressing_the_action_button_on_list_starts_a_new_report_list_entry() {
        onView(withId(R.id.action)).perform(click())
        onView(withId(R.id.report_view)).check(matches(isDisplayed()))
        onView(withId(R.id.id)).check(matches(isDisplayed())).check(matches(withText(EmptyUUID.toString())))
        onView(withId(R.id.brand)).check(matches(isDisplayed())).check(matches(withText(R.string.select_brand)))
        onView(withId(R.id.model)).check(matches(isDisplayed())).check(matches(withText(R.string.select_model)))
        onView(withId(R.id.name)).check(matches(isDisplayed())).check(matches(withText("")))
        onView(withId(R.id.description)).check(matches(isDisplayed())).check(matches(withText("")))
        onView(withId(R.id.action)).check(matches(isDisplayed()))
        onView(withId(R.id.damages)).check(matches(isDisplayed()))
            .check(matches(hasChildCount(0)))
            .check { v, _ ->
                assertEquals(0, (v as RecyclerView).adapter!!.itemCount)
            }

    }

    @Test
    fun pressing_on_a_report_shows_the_specific_entry() {

    }

    @Test
    fun swiping_on_a_report_deletes_it_and_shows_undo() {

    }

    @Test
    fun after_swiping_on_a_report_pressing_undo_restores_it() {

    }

}
