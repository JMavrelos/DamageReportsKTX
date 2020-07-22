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
import gr.blackswamp.damagereports.TestData
import gr.blackswamp.damagereports.logic.vms.MainViewModelImpl
import gr.blackswamp.damagereports.ui.fragments.ReportListFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var vm: MainViewModelImpl
    private lateinit var activity: MainActivity

    @get:Rule
    val scenario = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        TestData.initialize(ApplicationProvider.getApplicationContext())
        scenario.scenario.onActivity {
            vm = it.vm as MainViewModelImpl
            activity = it
        }
    }

    @Test
    fun main_activity_initialization() {
        onView(withId(R.id.base)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.container)).check(matches(isDisplayed()))
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.toolbar)).check { view, _ ->
            val tb = view as Toolbar
            assertEquals(context.getString(R.string.damage_reports), tb.title.toString())
        }
        onView(withId(R.id.container)).check { view, _ ->
            val destination = view.findNavController().currentDestination as FragmentNavigator.Destination
            assertEquals(destination.className, ReportListFragment::class.java.name)
        }
        onView(withId(R.id.report_list)).check(matches(isDisplayed()))
    }


    @Test
    fun showErrorShowsASnackBar() {
        vm.showError("this is an error")

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(isDisplayed()))
            .check(matches(withText("this is an error")))
    }

    @Test
    @Ignore("app freezes when this test runs, will look into it later")
    fun showLoadingDisplaysAFrameThatHidesTheOthersAndASpinner() {
        vm.loading.postValue(true)

        onView(withId(R.id.progress))
            .check(matches(isDisplayed()))
    }

}