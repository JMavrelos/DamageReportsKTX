package gr.blackswamp.damagereports.ui.reports

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import gr.blackswamp.core.MainCoroutineScopeRule
import gr.blackswamp.core.TestDispatchers
import gr.blackswamp.core.TestLog
import gr.blackswamp.damagereports.App
import gr.blackswamp.damagereports.data.repos.IReportRepository
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset

@ExperimentalCoroutinesApi
class ReportViewModelTest {
    private val repo: IReportRepository = mock(IReportRepository::class.java)
    private val app: App = mock(App::class.java)
    private val vm = ReportViewModel(repo, app, TestDispatchers, TestLog)

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        reset(repo, app)
    }

    @Test
    fun `when the app starts load all reports`() {

    }
}