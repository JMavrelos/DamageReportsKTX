package gr.blackswamp.damagereports.data.repos

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import gr.blackswamp.core.MainCoroutineScopeRule
import gr.blackswamp.core.TestDispatchers
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.prefs.IPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.sql.SQLException
import java.util.*


@ExperimentalCoroutinesApi
class ReportRepositoryTest {
    companion object {
        const val FILTER = "12j3kj1lk23mm.,asd"
        const val ERROR = "kj3l1"
    }

    private val db = mock<IDatabase>()
    private val prefs = mock<IPreferences>()
    private val vm: IReportRepository = ReportRepository(db, prefs, TestDispatchers)
    private val dao = mock<ReportDao>()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Before
    fun setUp() {
        reset(db, prefs)
        whenever(db.reportDao).thenReturn(dao)
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
            whenever(dao.reportHeaders(FILTER)).thenReturn(mock())
            vm.getReportHeaders(FILTER)
            verify(dao).reportHeaders(FILTER)
        }
    }

    @Test
    fun `calling get report headers with an error propagates the error`() {
        val error = SQLiteException(ERROR)
        whenever(dao.reportHeaders(FILTER)).thenThrow(error)
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