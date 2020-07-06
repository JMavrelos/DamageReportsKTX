package gr.blackswamp.damagereports.ui.fragments

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gr.blackswamp.core.testing.scrollToPosition
import gr.blackswamp.core.testing.withRecyclerView
import gr.blackswamp.core.util.toDateString
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.TestData
import org.hamcrest.core.IsNot
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinComponent

typealias HeaderEntry = Pair<String, String?>

@RunWith(AndroidJUnit4ClassRunner::class)
class ReportListFragmentTest : KoinComponent {
    private lateinit var scenario: FragmentScenario<ReportListFragment>
    private lateinit var reportHeaderData: List<HeaderEntry>

    @Before
    fun setUp() {
        TestData.initialize(ApplicationProvider.getApplicationContext())
        reportHeaderData = TestData.REPORTS
            .asSequence()
            .sortedByDescending { it.created }
            .groupBy { it.created.toDateString() }
            .map { entry ->
                listOf(entry.key to null, *entry.value.map { it.name to it.description }.toTypedArray())
            }.flatten()

        scenario = launchFragmentInContainer<ReportListFragment>(
            bundleOf()
            , R.style.AppTheme
            , factory = null
        )
    }

    @Test
    fun fragment_shows_correctly() {
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.report_list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check { view, _ -> assertEquals(reportHeaderData.size, (view as RecyclerView).adapter!!.itemCount) }

        reportHeaderData.forEachIndexed { index, entity ->
            onView(withId(R.id.list)).perform(scrollToPosition(index))
            if (entity.second == null) {
                onView(withRecyclerView(R.id.list).atPosition(index)).check(matches(withText(entity.first)))
            } else {
                onView(withRecyclerView(R.id.list).atPositionOnView(index, R.id.report_name)).check(matches(withText(entity.first)))
                onView(withRecyclerView(R.id.list).atPositionOnView(index, R.id.report_description)).check(matches(withText(entity.second)))
            }
        }

        onView(withId(R.id.action)).check(matches(isDisplayed()))

        onView(withId(R.id.theme_selection)).check(matches(IsNot(isDisplayed())))


    }
}