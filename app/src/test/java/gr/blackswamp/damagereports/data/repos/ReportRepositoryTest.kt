package gr.blackswamp.damagereports.data.repos

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import gr.blackswamp.core.MainCoroutineScopeRule
import gr.blackswamp.core.TestDispatchers
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.prefs.IPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.util.*


@ExperimentalCoroutinesApi
class ReportRepositoryTest : KoinTest {
    companion object {
        const val FILTER = "12j3kj1lk23mm.,asd"
        const val ERROR = "kj3l1"
        @BeforeClass
        @JvmStatic
        fun initialize() {
            startKoin {
                modules(emptyList())
            }
        }

        @AfterClass
        @JvmStatic
        fun dispose() {
            stopKoin()
        }
    }

    private val db = mock<IDatabase>()
    private val prefs = mock<IPreferences>()
    private lateinit var vm: IReportRepository
    private val dao = mock<ReportDao>()
    private val module = module {
        single { db }
        single { prefs }
        single<IDispatchers> { TestDispatchers }
    }


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Before
    fun setUp() {
        reset(db, prefs)
        whenever(db.reportDao).thenReturn(dao)
        loadKoinModules(module)
        vm = ReportRepository()
    }

    @After
    fun tearDown() {
        unloadKoinModules(module)
    }

    @Test
    fun `changing theme alters value`() {
        runBlockingTest {
            whenever(prefs.darkTheme).thenReturn(false)
            vm.switchTheme()
            verify(prefs).darkTheme = true
        }
    }

    @Test
    fun `calling get report headers creates new livedata from db`() {
        runBlockingTest {
            whenever(dao.loadReportHeaders(FILTER)).thenReturn(mock())
            vm.getReportHeaders(FILTER)
            verify(dao).loadReportHeaders(FILTER)
        }
    }

    @Test
    fun `calling get report headers with an error propagates the error`() {
        val error = SQLiteException(ERROR)
        whenever(dao.loadReportHeaders(FILTER)).thenThrow(error)
        val response = vm.getReportHeaders(FILTER)
        assertTrue(response.hasError)
        assertEquals(error, response.error)
    }

    @Test
    fun `delete report calls repo`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            vm.deleteReport(id)
            verify(dao).deleteReportById(id)
        }
    }

    @Test
    fun `delete report with error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val error = SQLiteException()
            whenever(dao.deleteReportById(id)).thenThrow(error)

            val response = vm.deleteReport(id)

            verify(dao).deleteReportById(id)
            assertNotNull(response)
            assertEquals(error, response)
        }
    }
}