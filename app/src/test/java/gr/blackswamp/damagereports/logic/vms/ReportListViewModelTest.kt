package gr.blackswamp.damagereports.logic.vms

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.data.Response
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.testing.KoinUnitTest
import gr.blackswamp.core.testing.MainCoroutineScopeRule
import gr.blackswamp.core.testing.TestDispatcher
import gr.blackswamp.core.testing.getOrAwait
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.repos.ReportListRepository
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.logic.commands.ReportListCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.model.BrandData
import gr.blackswamp.damagereports.logic.model.ModelData
import gr.blackswamp.damagereports.logic.model.ReportData
import gr.blackswamp.damagereports.logic.vms.ReportListViewModelImpl.Companion.LIST_PAGE_SIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import java.util.*

@ExperimentalCoroutinesApi
class ReportListViewModelTest : KoinUnitTest() {
    companion object {
        private const val FILTER = "a filter"
        private const val ERROR = " there was an error"
    }

    private val repo: ReportListRepository = mock(ReportListRepository::class.java)
    private val parent = mock(FragmentParent::class.java)
    override val modules: Module = module {
        single<Dispatcher> { TestDispatcher }
        single { repo }
    }

    private lateinit var vm: ReportListViewModelImpl

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    override fun setUp() {
        super.setUp()
        vm = ReportListViewModelImpl(app, parent, false)
        reset(repo)
    }

    @Test
    fun `load reports with no filter from the start`() {
        runBlocking {
            assertNull(vm.filter.value)
            assertNull(vm.reportHeaderList.value)

            whenever(repo.getReportHeaders("")).thenReturn(Response.success(StaticDataSource.factory(listOf())))
            whenever(repo.clearDeleted()).thenReturn(Response.success())

            vm.initialize()

            assertEquals(0, vm.reportHeaderList.getOrAwait().count())
            assertEquals("", vm.filter.value)
            verify(repo).getReportHeaders("")
        }
    }

    @Test
    fun `on initialization call clearing all unused entities`() {
        runBlocking {
            whenever(repo.clearDeleted()).thenReturn(Response.success())

            vm.initialize()

            verify(repo).clearDeleted()
            verify(parent).showLoading(false)
            verify(parent, never()).showError(anyString())
        }
    }

    @Test
    fun `when clearing fails an error is shown but the filter is still updated`() {
        runBlocking {
            whenever(repo.clearDeleted()).thenReturn(Response.failure(ERROR))

            vm.initialize()

            verify(repo).clearDeleted()
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertEquals("", vm.filter.value)
        }
    }

    @Test
    fun `when there is an error while loading show an empty list and pop a message`() {
        assertNull(vm.filter.value)
        assertNull(vm.reportHeaderList.value)

        val error = SQLiteException("Hello world")
        whenever(repo.getReportHeaders(FILTER)).thenReturn(Response.failure(error))

        vm.newReportFilter(FILTER, true)

        assertEquals(FILTER, vm.filter.value)
        assertEquals(0, vm.reportHeaderList.getOrAwait().count())
        verify(repo).getReportHeaders(FILTER)
    }

    @Test
    fun `when the filter changes the results change`() {
        assertNull(vm.filter.value)
        assertNull(vm.reportHeaderList.value)
        val expected = UnitTestData.REPORT_HEADERS.map { it.toData() }
        whenever(repo.getReportHeaders(FILTER)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))

        vm.newReportFilter(FILTER, true)

        val values = vm.reportHeaderList.getOrAwait().toList()
        assertEquals(FILTER, vm.filter.value)
        verify(repo).getReportHeaders(FILTER)
        assertEquals(LIST_PAGE_SIZE * 3, values.size)
        assertEquals(LIST_PAGE_SIZE * 3, expected.map { it.id }.intersect(values.map { it.id }).size)

    }

    @Test
    fun `when the report is deleted an undo message shows and its id is temporarily saved in memory in case of un-delete`() {
        runBlockingTest {
            val deleted = UnitTestData.REPORT_HEADERS.random()
            whenever(repo.deleteReport(deleted.id)).thenReturn(Response.success())

            vm.deleteReport(deleted.id)

            verify(parent).showLoading(false)
            assertTrue(vm.showUndo.getOrAwait())
            assertEquals(deleted.id, vm.lastDeleted.value)
        }
    }

    @Test
    fun `when the report is deleted and there was a problem then the error shows`() {
        runBlockingTest {
            val deleted = UnitTestData.REPORT_HEADERS.random()
            whenever(repo.deleteReport(deleted.id)).thenReturn(Response.failure(ERROR))

            vm.deleteReport(deleted.id)

            verify(repo).deleteReport(deleted.id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertFalse(vm.showUndo.getOrAwait())
        }
    }

    @Test
    fun `when the report is undeleted then the last deleted value is cleared`() {
        runBlockingTest {
            val deleted = UnitTestData.REPORT_HEADERS.random()
            whenever(repo.deleteReport(deleted.id)).thenReturn(Response.success())
            whenever(repo.restoreReport(deleted.id)).thenReturn(Response.success())

            vm.deleteReport(deleted.id)
            vm.undoLastDelete()

            verify(repo).restoreReport(deleted.id)
            verify(parent, times(2)).showLoading(false)
            verify(parent, never()).showError(anyString())
            assertFalse(vm.showUndo.value ?: false)
        }
    }

    @Test
    fun `when we try to undelete with no last deleted id an error shows`() {
        runBlockingTest {
            vm.undoLastDelete()

            verifyNoMoreInteractions(repo)

            verify(parent).showLoading(false)
            verify(parent).showError(APP_STRING)
            verify(app).getString(R.string.error_un_deleting_no_saved_value)
            assertFalse(vm.showUndo.value ?: false)
        }
    }

    @Test
    fun `when the report is un-deleted and there is a problem the error shows and last value is cleared`() {
        runBlockingTest {
            val deleted = UnitTestData.REPORT_HEADERS.random()
            whenever(repo.deleteReport(deleted.id)).thenReturn(Response.success())
            whenever(repo.restoreReport(deleted.id)).thenReturn(Response.failure(ERROR))

            vm.deleteReport(deleted.id)
            vm.undoLastDelete()

            verify(repo).restoreReport(deleted.id)
            verify(parent, times(2)).showLoading(false)
            verify(parent).showError(ERROR)
            assertFalse(vm.showUndo.value ?: false)
        }
    }

    @Test
    fun `dismissing un-delete clears last deleted value`() {

        vm.deleteReport(UnitTestData.REPORT_HEADERS.random().id)
        vm.dismissedUndo()

        assertFalse(vm.showUndo.value ?: false)
        assertNull(vm.lastDeleted.value)
    }

    @Test
    fun `selecting a report displays it on screen`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val report = ReportData(
                id,
                "a name",
                "a description",
                BrandData(UUID.randomUUID(), "a brand"),
                ModelData(UUID.randomUUID(), " a model", UUID.randomUUID()),
                Date(0)
            )
            whenever(repo.loadReport(id)).thenReturn(Response.success(report))
            vm.selectReport(id)

            verify(parent).showLoading(false)
            verify(parent, never()).showError(anyString())
        }
    }

    @Test
    fun `long pressing a report displays it for edit`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val report = ReportData(
                id,
                "a name",
                "a description",
                BrandData(UUID.randomUUID(), "a brand"),
                ModelData(UUID.randomUUID(), " a model", UUID.randomUUID()),
                Date()
            )
            whenever(repo.loadReport(id)).thenReturn(Response.success(report))
            vm.editReport(id)
        }
    }

    @Test
    fun `when selecting a report has a problem an error is shown`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(repo.loadReport(id)).thenReturn(Response.failure(ERROR))

            vm.selectReport(id)
            assertNull(vm.command.value)
            verify(parent).showError(ERROR)
            verify(parent).showLoading(false)
        }
    }

    @Test
    fun `when editing a report has a problem an error is shown`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(repo.loadReport(id)).thenReturn(Response.failure(ERROR))
            vm.editReport(id)

            verify(parent).showLoading(false)
            assertNull(vm.command.value)
            verify(parent).showError(ERROR)
        }
    }

    @Test
    fun `new report shows correctly`() {
        runBlockingTest {
            val now = Date().time

            vm.newReport()

            val command = vm.command.value!!
            assertTrue(command is ReportListCommand.ShowReport)
            val showCommand = command as ReportListCommand.ShowReport
            val report = showCommand.report as ReportData
            assertTrue(showCommand.inEditMode)
            assertEquals(EmptyUUID, report.id)
            assertTrue(report.created.time - now in 0..100) //this may fail if the test takes too much but 100 ms is long enough
            assertEquals(EmptyUUID, report.id)
            assertNull(report.brandName)
            assertNull(report.modelName)
            assertNull(report.brand)
            assertNull(report.model)
            assertEquals("", report.description)
            assertEquals("", report.name)
            assertFalse(report.changed)
        }
    }

    @Test
    fun `when the theme changes then the change is applied and a message to close the selector is set`() {
        vm.changeTheme(ThemeSetting.Auto)

        verify(repo).setTheme(ThemeSetting.Auto)
        assertNull(vm.themeSelection.value)

        reset(repo)

        vm.changeTheme(ThemeSetting.System)

        verify(repo).setTheme(ThemeSetting.System)
        assertNull(vm.themeSelection.value)

        reset(repo)

        vm.changeTheme(ThemeSetting.Light)

        verify(repo).setTheme(ThemeSetting.Light)
        assertNull(vm.themeSelection.value)

        reset(repo)

        vm.changeTheme(ThemeSetting.Dark)

        verify(repo).setTheme(ThemeSetting.Dark)
        assertNull(vm.themeSelection.value)
    }

    @Test
    fun `check the interaction of opening and closing the theme`() {
        whenever(repo.themeSetting).thenReturn(ThemeSetting.System)
        vm.showThemeSettings()

        verify(repo).themeSetting
        assertEquals(ThemeSetting.System, vm.themeSelection.value)

        vm.closeThemeSelection()

        verifyNoMoreInteractions(repo)
        assertNull(vm.themeSelection.value)
    }
}