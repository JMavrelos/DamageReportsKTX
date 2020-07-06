package gr.blackswamp.damagereports.ui.fragments

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.TestData
import gr.blackswamp.damagereports.logic.vms.MainViewModelImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinComponent
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4ClassRunner::class)
class ReportViewFragmentTest : KoinComponent {
    private val parent = mock(MainViewModelImpl::class.java)


    @Before
    fun setUp() {
        TestData.initialize(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun fragment_loads_correctly_for_new_insert() {
        val scenario = launchFragmentInContainer<ReportViewFragment>(
            bundleOf("report" to EmptyUUID, "inEditMode" to true)
        )
        onView(withId(R.id.report_view)).check(matches(isDisplayed()))
    }
}
