package gr.blackswamp.damagereports.data.repos

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import gr.blackswamp.core.MainCoroutineScopeRule
import gr.blackswamp.core.TestDispatchers
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.prefs.IPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers.anyString
import java.util.*


@ExperimentalCoroutinesApi
class ReportRepositoryTest : KoinTest {
    companion object {
        private val app = mock<Application>()
        const val FILTER = "12j3kj1lk23mm.,asd"
        const val ERROR = "there was a problem"
        @BeforeClass
        @JvmStatic
        fun initialize() {
            startKoin {
                modules(emptyList())
                androidContext(app)
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
    private lateinit var repo: IReportRepository
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
        reset(db, prefs, app)
        whenever(db.reportDao).thenReturn(dao)
        loadKoinModules(module)
        repo = ReportRepository()
    }

    @After
    fun tearDown() {
        unloadKoinModules(module)
    }

    @Test
    fun `changing theme alters value`() {
        runBlockingTest {
            whenever(prefs.darkTheme).thenReturn(false)
            repo.switchTheme()
            verify(prefs).darkTheme = true
        }
    }

    @Test
    fun `calling get report headers creates new livedata from db`() {
        runBlockingTest {
            whenever(dao.loadReportHeaders(FILTER)).thenReturn(mock())
            repo.getReportHeaders(FILTER)
            verify(dao).loadReportHeaders(FILTER)
        }
    }

    @Test
    fun `calling get report headers with an error propagates the error`() {
        val error = SQLiteException(ERROR)
        whenever(dao.loadReportHeaders(FILTER)).thenThrow(error)
        val response = repo.getReportHeaders(FILTER)
        assertTrue(response.hasError)
        assertEquals(error, response.error)
    }

    @Test
    fun `delete report calls repo`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(dao.flagReportDeleted(id)).thenReturn(1)

            val result = repo.deleteReport(id)

            verify(dao).flagReportDeleted(id)
            assertFalse(result.hasError)
        }
    }

    @Test
    fun `delete report with error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val error = SQLiteException("$ERROR with sqlite")
            whenever(dao.flagReportDeleted(id)).thenThrow(error)
            whenever(app.getString(eq(R.string.error_deleting), anyString())).thenReturn(ERROR)

            val response = repo.deleteReport(id)

            verify(dao).flagReportDeleted(id)
            assertEquals(ERROR, response.errorMessage)
        }
    }

    @Test
    fun `delete report with no affected values shows error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(dao.flagReportDeleted(id)).thenReturn(0)
            whenever(app.getString(R.string.error_report_not_found, id)).thenReturn(ERROR)

            val response = repo.deleteReport(id)

            verify(dao).flagReportDeleted(id)
            assertEquals(ERROR, response.errorMessage)
        }
    }


    @Test
    fun `un-delete report calls repo`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(dao.unFlagReportDeleted(id)).thenReturn(1)

            val result = repo.unDeleteReport(id)

            verify(dao).unFlagReportDeleted(id)
            assertFalse(result.hasError)
        }
    }

    @Test
    fun `un-delete report with error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val error = SQLiteException("$ERROR with sqlite")
            whenever(dao.unFlagReportDeleted(id)).thenThrow(error)
            whenever(app.getString(eq(R.string.error_un_deleting), anyString())).thenReturn(ERROR)

            val response = repo.unDeleteReport(id)

            verify(dao).unFlagReportDeleted(id)
            assertEquals(ERROR, response.errorMessage)
        }
    }

    @Test
    fun `un-delete report with no affected values shows error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(dao.unFlagReportDeleted(id)).thenReturn(0)
            whenever(app.getString(R.string.error_no_deleted_report, id)).thenReturn(ERROR)

            val response = repo.unDeleteReport(id)

            verify(dao).unFlagReportDeleted(id)
            assertEquals(ERROR, response.errorMessage)
        }
    }
}