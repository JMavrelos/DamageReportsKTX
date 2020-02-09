//package gr.blackswamp.damagereports.ui.reports.fragments
//
//import androidx.fragment.app.testing.launchFragmentInContainer
//import androidx.lifecycle.Lifecycle
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.ViewAssertion
//import androidx.test.espresso.assertion.ViewAssertions
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.espresso.matcher.ViewMatchers.withText
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import gr.blackswamp.core.coroutines.AppDispatchers
//import gr.blackswamp.core.coroutines.IDispatchers
//import gr.blackswamp.core.testing.AndroidKoinTest
//import gr.blackswamp.damagereports.R
//import gr.blackswamp.damagereports.TestApp
//import gr.blackswamp.damagereports.data.db.AppDatabase
//import gr.blackswamp.damagereports.data.db.AppDatabaseImpl
//import gr.blackswamp.damagereports.data.prefs.Preferences
//import gr.blackswamp.damagereports.data.prefs.PreferencesImpl
//import gr.blackswamp.damagereports.data.repos.ReportRepository
//import gr.blackswamp.damagereports.data.repos.ReportRepositoryImpl
//import gr.blackswamp.damagereports.vms.reports.ReportViewModelImpl
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.koin.android.ext.koin.androidApplication
//import org.koin.android.viewmodel.dsl.viewModel
//import org.koin.dsl.module
//import org.robolectric.annotation.Config
//
//@ExperimentalCoroutinesApi
//@RunWith(AndroidJUnit4::class)
//@Config(application = TestApp::class)
//class ReportListFragmentTest : AndroidKoinTest() {
//
//    override val modules = module {
//        single<IDispatchers> { AppDispatchers }
//        single<AppDatabase> {
//            Room.inMemoryDatabaseBuilder(
//                ApplicationProvider.getApplicationContext()
//                , AppDatabaseImpl::class.java
//            ).allowMainThreadQueries().build()
//        }
//        single<Preferences> { PreferencesImpl(androidApplication()) }
//        single<ReportRepository> { ReportRepositoryImpl() }
//        viewModel { ReportViewModelImpl(androidApplication(), true) }
//    }
//
//    @Before
//    override fun setUp() {
//        super.setUp()
//    }
//
//    @Test
//    fun `when the fragment is loaded then a list is shown`() {
//        val scenario = launchFragmentInContainer<ReportListFragment>()
//        scenario.moveToState(Lifecycle.State.RESUMED)
//        onView(withId(R.id.toolbar))
//            .check(ViewAssertions.matches(withText() withText()))
//
//
//    }
//
//}