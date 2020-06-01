package gr.blackswamp.damagereports.ui.fragments

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.TestData
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinComponent

@RunWith(AndroidJUnit4ClassRunner::class)
class ReportListFragmentTest : KoinComponent {

    @Before
    fun setUp() {
        TestData.initialize(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun fragment_shows_correctly() {
        val scenario = launchFragmentInContainer<ReportListFragment>(
            bundleOf()
            , R.style.AppTheme
            , factory = null
        )
        onView(withId(R.id.report_list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check { v, _ ->
            val r = v as RecyclerView
            assertEquals(TestData.REPORTS.size, r.adapter!!.itemCount)
        }
    }
}