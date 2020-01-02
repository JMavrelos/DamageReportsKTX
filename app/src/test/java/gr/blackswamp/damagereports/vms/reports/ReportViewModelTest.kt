package gr.blackswamp.damagereports.vms.reports

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import gr.blackswamp.core.MainCoroutineScopeRule
import gr.blackswamp.core.TestDispatchers
import gr.blackswamp.core.TestLog
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.data.Response
import gr.blackswamp.core.logging.ILog
import gr.blackswamp.core.test
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.repos.IReportRepository
import gr.blackswamp.damagereports.util.StaticDataSource
import gr.blackswamp.damagereports.vms.reports.ReportViewModel.Companion.LIST_PAGE_SIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset

@ExperimentalCoroutinesApi
class ReportViewModelTest : KoinTest {
    companion object {
        private val app = mock(TestApp::class.java)
        private const val FILTER = "a filter"
        private const val ERROR = " there was an error"

        @BeforeClass
        @JvmStatic
        fun initialize() {
            startKoin {
                androidContext(app)
                modules(emptyList())
            }
        }

        @AfterClass
        @JvmStatic
        fun dispose() {
            stopKoin()
        }
    }

    private val repo: IReportRepository = mock(IReportRepository::class.java)
    private lateinit var modules: Module
    private lateinit var vm: ReportViewModel

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        modules = module {
            single<IDispatchers> { TestDispatchers }
            single<ILog> { TestLog }
            single { repo }
            single { app }
        }
        loadKoinModules(modules)
        vm = ReportViewModel(app, false)
        reset(repo, app)
    }

    @After
    fun tearDown() {
        unloadKoinModules(modules)
    }

    @Test
    fun `load reports with no filter from the start`() {
        assertNull(vm.filter.value)
        assertNull(vm.reportHeaderList.value)
        vm.reportHeaderList.test()
        whenever(repo.getReportHeaders("")).thenReturn(Response.success(StaticDataSource.factory(listOf())))

        vm.initialize()
        assertEquals("", vm.filter.value)
        verify(repo).getReportHeaders("")
        assertEquals(0, vm.reportHeaderList.value!!.count())
    }

    @Test
    fun `when there is an error while loading show an empty list and pop a message`() {
        assertNull(vm.filter.value)
        assertNull(vm.reportHeaderList.value)
        vm.reportHeaderList.test()
        val error = SQLiteException("Hello world")
        whenever(repo.getReportHeaders(FILTER)).thenReturn(Response.failure(error))

        vm.newReportFilter(FILTER, true)

        assertEquals(FILTER, vm.filter.value)
        verify(repo).getReportHeaders(FILTER)
        assertEquals(0, vm.reportHeaderList.value!!.count())
    }

    @Test
    fun `when the filter changes the results change`() {
        assertNull(vm.filter.value)
        assertNull(vm.reportHeaderList.value)
        vm.reportHeaderList.test()
        val expected = UnitTestData.REPORT_HEADERS

        whenever(repo.getReportHeaders(FILTER)).thenReturn(Response.success(StaticDataSource.factory(expected)))

        vm.newReportFilter(FILTER, true)

        assertEquals(FILTER, vm.filter.value)
        verify(repo).getReportHeaders(FILTER)

        val values = vm.reportHeaderList.value!!.toList()

        assertEquals(LIST_PAGE_SIZE * 3, values.size)
        assertEquals(LIST_PAGE_SIZE * 3, expected.map { it.id }.intersect(values.map { it.id }).size)

    }

    @Test
    fun `when the report is deleted an undo message shows and its id is temporarily saved in memory in case of un-delete`() {
        runBlockingTest {
            val deleted = UnitTestData.REPORT_HEADERS.random()
            whenever(repo.deleteReport(deleted.id)).thenReturn(null)
            vm.showUndo.test() //we add an observer otherwise it won't trigger

            vm.deleteReport(deleted.id)

            assertFalse(vm.loading.value!!)
            assertTrue(vm.showUndo.value!!)
            assertEquals(deleted.id, vm.lastDeleted.value)
        }
    }

    @Test
    fun `when the report is deleted and there was a problem then the error shows`() {
        runBlockingTest {
            val deleted = UnitTestData.REPORT_HEADERS.random()
            whenever(repo.deleteReport(deleted.id)).thenReturn(ERROR)
            vm.showUndo.test()

            vm.deleteReport(deleted.id)

            verify(repo).deleteReport(deleted.id)
            assertEquals(ERROR, vm.error.value)
            assertFalse(vm.loading.value!!)
            assertFalse(vm.showUndo.value ?: false)
        }
    }

    @Test
    fun `when the report is undeleted then the last deleted value is cleared`() {
        runBlockingTest {
            val deleted = UnitTestData.REPORT_HEADERS.random()
            vm.deleteReport(deleted.id)
            whenever(repo.unDeleteReport(deleted.id)).thenReturn(null)

            vm.undoLastDelete()

            verify(repo).unDeleteReport(deleted.id)
            assertNull(vm.error.value)
            assertFalse(vm.loading.value!!)
            assertFalse(vm.showUndo.value ?: false)
        }
    }

    @Test
    fun `when we try to undelete with no last deleted id an error shows`() {
        runBlockingTest {
            whenever(app.getString(R.string.error_un_deleting_no_saved_value)).thenReturn(ERROR)

            vm.undoLastDelete()

            verifyZeroInteractions(repo)
            assertEquals(ERROR, vm.error.value)
            assertFalse(vm.showUndo.value ?: false)
            assertFalse(vm.loading.value!!)
        }
    }

    @Test
    fun `when the report is un-deleted and there is a problem the error shows and last value is cleared`() {
        runBlockingTest {
            val deleted = UnitTestData.REPORT_HEADERS.random()
            vm.deleteReport(deleted.id)
            whenever(repo.unDeleteReport(deleted.id)).thenReturn(ERROR)

            vm.undoLastDelete()

            verify(repo).unDeleteReport(deleted.id)
            assertEquals(ERROR, vm.error.value)
            assertFalse(vm.showUndo.value ?: false)
            assertFalse(vm.loading.value!!)
        }
    }

    @Test
    fun `dismissing un-delete clears last deleted value`() {

        vm.deleteReport(UnitTestData.REPORT_HEADERS.random().id)
        vm.dismissedUndo()

        assertFalse(vm.showUndo.value ?: false)
        assertNull(vm.lastDeleted.value)
    }
}