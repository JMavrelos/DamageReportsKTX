package gr.blackswamp.damagereports.data.repos

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.testing.KoinUnitTest
import gr.blackswamp.core.testing.MainCoroutineScopeRule
import gr.blackswamp.core.testing.TestDispatcher
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.TestData
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.logic.model.BrandData
import gr.blackswamp.damagereports.logic.model.ModelData
import gr.blackswamp.damagereports.logic.model.ReportData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import java.util.*


@ExperimentalCoroutinesApi
class ReportListRepositoryTest : KoinUnitTest() {
    companion object {
        const val FILTER = "12j3kj1lk23mm.,asd"
    }

    private val db = mock<AppDatabase>()
    private val prefs = mock<Preferences>()
    private lateinit var repo: ReportListRepository
    private val dao = mock<ReportDao>()
    private val mDao = mock<ModelDao>()
    private val bDao = mock<BrandDao>()

    override val modules = module {
        single { db }
        single { prefs }
        single<Dispatcher> { TestDispatcher }
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Before
    override fun setUp() {
        super.setUp()
        reset(db, prefs)
        whenever(db.reportDao).thenReturn(dao)
        whenever(db.brandDao).thenReturn(bDao)
        whenever(db.modelDao).thenReturn(mDao)
        repo = ReportListRepositoryImpl()
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
        val error = SQLiteException("error with sqlite")
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
            val error = SQLiteException("error with sqlite")
            whenever(dao.flagReportDeleted(id)).thenThrow(error)

            val response = repo.deleteReport(id)

            verify(dao).flagReportDeleted(id)
            assertEquals(APP_STRING, response.errorMessage)
            verify(app).getString(R.string.error_deleting, error.message ?: error::class.java.name)
            verifyNoMoreInteractions(app)
        }
    }

    @Test
    fun `delete report with no affected values shows error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(dao.flagReportDeleted(id)).thenReturn(0)

            val response = repo.deleteReport(id)

            verify(dao).flagReportDeleted(id)
            assertEquals(APP_STRING, response.errorMessage)
            verify(app).getString(R.string.error_report_not_found, id)
            verifyNoMoreInteractions(app)
        }
    }


    @Test
    fun `un-delete report calls repo`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(dao.unFlagReportDeleted(id)).thenReturn(1)

            val result = repo.restoreReport(id)

            verify(dao).unFlagReportDeleted(id)
            assertFalse(result.hasError)
        }
    }

    @Test
    fun `un-delete report with error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val error = SQLiteException("error with sqlite")
            whenever(dao.unFlagReportDeleted(id)).thenThrow(error)

            val response = repo.restoreReport(id)

            verify(dao).unFlagReportDeleted(id)
            assertEquals(APP_STRING, response.errorMessage)
            verify(app).getString(R.string.error_un_deleting, error.message ?: error::class.java.name)
            verifyNoMoreInteractions(app)
        }
    }

    @Test
    fun `un-delete report with no affected values shows error`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            whenever(dao.unFlagReportDeleted(id)).thenReturn(0)

            val response = repo.restoreReport(id)

            verify(dao).unFlagReportDeleted(id)
            assertEquals(APP_STRING, response.errorMessage)
            verify(app).getString(R.string.error_no_deleted_report, id)
            verifyNoMoreInteractions(app)
        }
    }

    @Test
    fun `load report that does not exist`() {
        runBlockingTest {
            val report = TestData.REPORTS.random()
            whenever(dao.loadReportById(report.id)).thenReturn(null)

            val response = repo.loadReport(report.id)

            verify(dao).loadReportById(report.id)
            verifyNoMoreInteractions(mDao, bDao)
            assertTrue(response.hasError)
            assertEquals(APP_STRING, response.errorMessage)
            verify(app).getString(R.string.error_report_not_found, report.id)
        }
    }

    @Test
    fun `load report whose brand does not exist`() {
        runBlockingTest {
            val report = TestData.REPORTS.random()
            whenever(dao.loadReportById(report.id)).thenReturn(report)
            whenever(bDao.loadBrandById(report.brand)).thenReturn(null)

            val response = repo.loadReport(report.id)

            verify(dao).loadReportById(report.id)
            verify(bDao).loadBrandById(report.brand)
            verifyNoMoreInteractions(mDao)
            assertTrue(response.hasError)
            assertEquals(APP_STRING, response.errorMessage)
            verify(app).getString(R.string.error_brand_not_found, report.brand)
        }
    }


    @Test
    fun `load report whose model does not exist`() {
        runBlockingTest {
            val report = TestData.REPORTS.random()
            whenever(dao.loadReportById(report.id)).thenReturn(report)
            whenever(bDao.loadBrandById(report.brand)).thenReturn(TestData.BRANDS.first { it.id == report.brand })
            whenever(mDao.loadModelById(report.model)).thenReturn(null)


            val response = repo.loadReport(report.id)

            verify(dao).loadReportById(report.id)
            verify(mDao).loadModelById(report.model)
            verify(bDao).loadBrandById(report.brand)
            assertTrue(response.hasError)
            assertEquals(APP_STRING, response.errorMessage)
            verify(app).getString(R.string.error_model_not_found, report.model)
        }
    }


    @Test
    fun `load a report incorrect model-brand association`() {
        runBlockingTest {
            val report = TestData.REPORTS.random()

            whenever(dao.loadReportById(report.id)).thenReturn(report)
            whenever(bDao.loadBrandById(report.brand)).thenReturn(TestData.BRANDS.first { it.id == report.brand })
            whenever(mDao.loadModelById(report.model)).thenReturn(TestData.MODELS.filter { it.id != report.model }.random())

            val response = repo.loadReport(report.id)

            assertTrue(response.hasError)
            verify(dao).loadReportById(report.id)
            verify(mDao).loadModelById(report.model)
            verify(bDao).loadBrandById(report.brand)
            assertEquals(APP_STRING, response.errorMessage)
            verify(app).getString(R.string.error_invalid_model_brand)
        }
    }

    @Test
    fun `load a report with no problems`() {
        runBlockingTest {
            val report = TestData.REPORTS.random()
            val brand = TestData.BRANDS.first { it.id == report.brand }
            val model = TestData.MODELS.first { it.id == report.model }

            whenever(dao.loadReportById(report.id)).thenReturn(report)
            whenever(bDao.loadBrandById(report.brand)).thenReturn(brand)
            whenever(mDao.loadModelById(report.model)).thenReturn(model)

            val expected = ReportData(
                report.id,
                report.name,
                report.description,
                BrandData(brand.id, brand.name),
                ModelData(model.id, model.name, brand.id),
                report.created
            )

            val response = repo.loadReport(report.id)

            verify(dao).loadReportById(report.id)
            verify(mDao).loadModelById(report.model)
            verify(bDao).loadBrandById(report.brand)
            assertFalse(response.hasError)
            assertEquals(expected, response.get)
        }
    }
}