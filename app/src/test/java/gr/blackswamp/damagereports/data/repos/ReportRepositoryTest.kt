package gr.blackswamp.damagereports.data.repos

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.testing.MainCoroutineScopeRule
import gr.blackswamp.core.testing.TestDispatchers
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.vms.BrandData
import gr.blackswamp.damagereports.vms.ModelData
import gr.blackswamp.damagereports.vms.ReportData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.robolectric.annotation.Config
import java.util.*


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestApp::class)
class ReportRepositoryTest : KoinTest {
    companion object {
        const val FILTER = "12j3kj1lk23mm.,asd"
        const val ERROR = "there was a problem"
    }

    private val application = ApplicationProvider.getApplicationContext<TestApp>()
    private val db = mock<AppDatabase>()
    private val prefs = mock<Preferences>()
    private lateinit var repo: ReportRepository
    private val dao = mock<ReportDao>()
    private val mDao = mock<ModelDao>()
    private val bDao = mock<BrandDao>()

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
        whenever(db.brandDao).thenReturn(bDao)
        whenever(db.modelDao).thenReturn(mDao)
        TestApp.startKoin(module)
        repo = ReportRepositoryImpl()

    }

    @After
    fun tearDown() {
        TestApp.stopKoin(module)
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
            val expected = application.getString(R.string.error_deleting, error.message!!)
            whenever(dao.flagReportDeleted(id)).thenThrow(error)

            val response = repo.deleteReport(id)

            verify(dao).flagReportDeleted(id)
            assertEquals(expected, response.errorMessage)
        }
    }

    @Test
    fun `delete report with no affected values shows error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val expected = application.getString(R.string.error_report_not_found, id)
            whenever(dao.flagReportDeleted(id)).thenReturn(0)


            val response = repo.deleteReport(id)

            verify(dao).flagReportDeleted(id)
            assertEquals(expected, response.errorMessage)
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
            val expected = application.getString(R.string.error_un_deleting, error.message)
            whenever(dao.unFlagReportDeleted(id)).thenThrow(error)

            val response = repo.unDeleteReport(id)

            verify(dao).unFlagReportDeleted(id)
            assertEquals(expected, response.errorMessage)
        }
    }

    @Test
    fun `un-delete report with no affected values shows error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val expected = application.getString(R.string.error_no_deleted_report, id)
            whenever(dao.unFlagReportDeleted(id)).thenReturn(0)

            val response = repo.unDeleteReport(id)

            verify(dao).unFlagReportDeleted(id)
            assertEquals(expected, response.errorMessage)
        }
    }

    @Test
    fun `load report that does not exist`() {
        runBlockingTest {
            val report = UnitTestData.REPORTS.random()
            val expected = application.getString(R.string.error_report_not_found, report.id)
            whenever(dao.loadReportById(report.id)).thenReturn(null)

            val response = repo.loadReport(report.id)

            verify(dao).loadReportById(report.id)
            verifyZeroInteractions(mDao, bDao)
            assertTrue(response.hasError)
            assertEquals(expected, response.errorMessage)
        }
    }

    @Test
    fun `load report whose brand does not exist`() {
        runBlockingTest {
            val report = UnitTestData.REPORTS.random()
            val expected = application.getString(R.string.error_brand_not_found, report.brand)
            whenever(dao.loadReportById(report.id)).thenReturn(report)
            whenever(bDao.loadBrandById(report.brand)).thenReturn(null)

            val response = repo.loadReport(report.id)

            verify(dao).loadReportById(report.id)
            verify(bDao).loadBrandById(report.brand)
            verifyZeroInteractions(mDao)
            assertTrue(response.hasError)
            assertEquals(expected, response.errorMessage)
        }
    }


    @Test
    fun `load report whose model does not exist`() {
        runBlockingTest {
            val report = UnitTestData.REPORTS.random()
            val expected = application.getString(R.string.error_model_not_found, report.model)
            whenever(dao.loadReportById(report.id)).thenReturn(report)
            whenever(bDao.loadBrandById(report.brand)).thenReturn(UnitTestData.BRANDS.first { it.id == report.brand })
            whenever(mDao.loadModelById(report.model)).thenReturn(null)

            val response = repo.loadReport(report.id)

            verify(dao).loadReportById(report.id)
            verify(mDao).loadModelById(report.model)
            verify(bDao).loadBrandById(report.brand)
            assertTrue(response.hasError)
            assertEquals(expected, response.errorMessage)
        }
    }


    @Test
    fun `load a report incorrect model-brand association`() {
        runBlockingTest {
            val report = UnitTestData.REPORTS.random()
            val expected = application.getString(R.string.error_invalid_model_brand)
            whenever(dao.loadReportById(report.id)).thenReturn(report)
            whenever(bDao.loadBrandById(report.brand)).thenReturn(UnitTestData.BRANDS.first { it.id == report.brand })
            whenever(mDao.loadModelById(report.model)).thenReturn(UnitTestData.MODELS.filter { it.id != report.model }.random())

            val response = repo.loadReport(report.id)

            assertTrue(response.hasError)
            verify(dao).loadReportById(report.id)
            verify(mDao).loadModelById(report.model)
            verify(bDao).loadBrandById(report.brand)
            assertEquals(expected, response.errorMessage)
        }
    }

    @Test
    fun `load a report with no problems`() {
        runBlockingTest {
            val report = UnitTestData.REPORTS.random()
            val brand = UnitTestData.BRANDS.first { it.id == report.brand }
            val model = UnitTestData.MODELS.first { it.id == report.model }

            whenever(dao.loadReportById(report.id)).thenReturn(report)
            whenever(bDao.loadBrandById(report.brand)).thenReturn(brand)
            whenever(mDao.loadModelById(report.model)).thenReturn(model)

            val expected = ReportData(report.id, report.name, report.description, BrandData(brand.id, brand.name), ModelData(model.id, model.name, brand.id), report.created)

            val response = repo.loadReport(report.id)

            verify(dao).loadReportById(report.id)
            verify(mDao).loadModelById(report.model)
            verify(bDao).loadBrandById(report.brand)
            assertFalse(response.hasError)
            assertEquals(expected, response.get)
        }
    }
}