package gr.blackswamp.damagereports.data.db

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.room.Room
import androidx.room.paging.LimitOffsetDataSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import gr.blackswamp.core.db.countWhere
import gr.blackswamp.core.testing.getOrAwait
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestApp::class)
class ReportDaoTest {
    private lateinit var db: AppDatabaseImpl
    private lateinit var dao: ReportDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabaseImpl::class.java
        ).allowMainThreadQueries()
            .build()
        dao = db.reportDao
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `when the db is empty then we get 0 row count`() {
        runBlockingTest {
            val count = dao.count()
            assertEquals(0, count)
        }
    }

    @Test
    fun `try to insert a report with no correct brand or model`() {
        val report = ReportEntity(
            UUID.randomUUID(),
            "hello",
            "world",
            UnitTestData.MODELS[0].brand,
            UnitTestData.MODELS[0].id
        )
        runBlockingTest {
            var error: Throwable? = null
            try {
                dao.saveReport(report)
            } catch (e: Throwable) {
                error = e
            }
            assertNotNull(error)
            assertTrue(error is SQLiteConstraintException)
        }
    }

    @Test
    fun `insert report with valid brand but no model`() {
        runBlockingTest {
            val model = UnitTestData.MODELS.random()
            val brand = UnitTestData.BRANDS.filter { it.id == model.brand }.random()
            db.brandDao.insertBrand(brand)
            val report = ReportEntity(UUID.randomUUID(), "hello", "world", model.brand, model.id)

            var error: Throwable? = null
            try {
                dao.saveReport(report)
            } catch (e: Throwable) {
                error = e
            }

            assertNotNull(error)
            assertTrue(error is SQLiteConstraintException)
        }
    }

    @Test
    fun `insert report with valid model but no brand`() {
        runBlockingTest {
            val model = UnitTestData.MODELS.random()
            val brand = UnitTestData.BRANDS.filter { it.id == model.brand }.random()
            db.brandDao.insertBrand(brand)
            db.modelDao.insertModel(model)
            val report = ReportEntity(UUID.randomUUID(), "hello", "world", UUID.randomUUID(), model.id)

            var error: Throwable? = null
            try {
                dao.saveReport(report)
            } catch (e: Throwable) {
                error = e
            }

            assertNotNull(error)
            assertTrue(error is SQLiteConstraintException)
        }
    }


    @Test
    fun `insert report with no constraint problem`() {
        runBlockingTest {
            val model = UnitTestData.MODELS.random()
            val brand = UnitTestData.BRANDS.filter { it.id == model.brand }.random()
            db.brandDao.insertBrand(brand)
            db.modelDao.insertModel(model)
            val report = ReportEntity(UUID.randomUUID(), "hello", "world", model.brand, model.id)

            dao.saveReport(report)

            val count = db.countWhere(
                "reports",
                " name = '${report.name}' and description = '${report.description}' and brand='${report.brand}' and model = '${report.model}' and id = '${report.id}'"
            )
            assertEquals(1, count)

        }
    }

    // not needed for now, I'll keep them for a few versions in case they are
//    @Test
//    fun `delete a report`() {
//        runBlockingTest {
//            initData()
//            val deleted = UnitTestData.REPORTS[3]
//            val expected = db.count("reports") - 1
//
//            dao.deleteReportById(deleted.id)
//
//            val count = db.count("reports")
//
//            assertEquals(expected, count)
//        }
//    }
//
//    @Test
//    fun `delete a report that doesn't exist`() {
//        runBlockingTest {
//            initData()
//            val expected = db.count("reports")
//
//            dao.deleteReportById(UUID.randomUUID())
//
//            val count = db.count("reports")
//
//            assertEquals(expected, count)
//        }
//    }

    @Test
    fun `search a report with no arguments`() {
        runBlockingTest {
            initData()

            val result = (dao.loadReportHeaders("").create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(UnitTestData.REPORTS.size, UnitTestData.REPORTS.count { result.map { it.id }.contains(it.id) })
        }
    }


    @Test
    fun `search report by name`() {
        runBlockingTest {
            initData()
            val filter = "Hello World" //this is on purpose 11 characters so that the random models cannot possibly contain it in their name
            val expected = listOf(
                ReportEntity(UUID.randomUUID(), "5${filter}1", "", UnitTestData.BRANDS[7].id, UnitTestData.MODELS.first { it.brand == UnitTestData.BRANDS[7].id }.id)
                , ReportEntity(UUID.randomUUID(), "2${filter}2", "", UnitTestData.BRANDS[7].id, UnitTestData.MODELS.first { it.brand == UnitTestData.BRANDS[7].id }.id)
                , ReportEntity(UUID.randomUUID(), "3${filter}3", "", UnitTestData.BRANDS[7].id, UnitTestData.MODELS.first { it.brand == UnitTestData.BRANDS[7].id }.id)
                , ReportEntity(UUID.randomUUID(), "1${filter}4", "", UnitTestData.BRANDS[7].id, UnitTestData.MODELS.first { it.brand == UnitTestData.BRANDS[7].id }.id)
            )
            expected.forEach {
                dao.saveReport(it)
            }

            val result = (dao.loadReportHeaders(filter).create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(expected.size, expected.count { entity -> result.map { it.id }.contains(entity.id) })
        }
    }

    @Test
    fun `search report by description`() {
        runBlockingTest {
            initData()
            val filter = "Hello World" //this is on purpose 11 characters so that the random models cannot possibly contain it in their name
            val expected = listOf(
                ReportEntity(UUID.randomUUID(), "", "5${filter}1", UnitTestData.BRANDS[5].id, UnitTestData.MODELS.first { it.brand == UnitTestData.BRANDS[5].id }.id)
                , ReportEntity(UUID.randomUUID(), "", "2${filter}2", UnitTestData.BRANDS[5].id, UnitTestData.MODELS.first { it.brand == UnitTestData.BRANDS[5].id }.id)
                , ReportEntity(UUID.randomUUID(), "", "3${filter}3", UnitTestData.BRANDS[5].id, UnitTestData.MODELS.first { it.brand == UnitTestData.BRANDS[5].id }.id)
                , ReportEntity(UUID.randomUUID(), "", "1${filter}4", UnitTestData.BRANDS[5].id, UnitTestData.MODELS.first { it.brand == UnitTestData.BRANDS[5].id }.id)
            )

            expected.forEach {
                dao.saveReport(it)
            }

            val result = (dao.loadReportHeaders(filter).create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(expected.size, expected.count { entity -> result.map { it.id }.contains(entity.id) })
        }
    }

    @Test
    fun `flagging report as deleted works`() {
        runBlockingTest {
            initData()
            val toDelete = UnitTestData.REPORTS.random()

            val response = dao.flagReportDeleted(toDelete.id)

            assertEquals(1, response)
            val count = db.countWhere("reports", " id = '${toDelete.id}' and deleted = 1")
            assertEquals(1, count)
        }
    }

    @Test
    fun `flagging an already deleted report returns that nothing is affected`() {
        runBlockingTest {
            initData()
            val report = UnitTestData.REPORTS.random()

            dao.flagReportDeleted(report.id)

            var count = db.countWhere("reports", " id = '${report.id}' and deleted = 1")
            assertEquals(1, count)

            val response = dao.flagReportDeleted(report.id)
            assertEquals(0, response)
            count = db.countWhere("reports", " id = '${report.id}' and deleted = 1")
            assertEquals(1, count)
        }
    }

    @Test
    fun `unDeleting a report works`() {
        runBlockingTest {
            initData()
            val report = UnitTestData.REPORTS.random()

            dao.flagReportDeleted(report.id)

            var count = db.countWhere("reports", " id = '${report.id}' and deleted = 1")
            assertEquals(1, count)

            val response = dao.unFlagReportDeleted(report.id)
            assertEquals(1, response)
            count = db.countWhere("reports", " id = '${report.id}' and deleted = 0")
            assertEquals(1, count)
        }
    }

    @Test
    fun `unDeleting a report that is not deleted returns that nothing is affected`() {
        runBlockingTest {

            initData()
            val toDelete = UnitTestData.REPORTS.random()

            val response = dao.unFlagReportDeleted(toDelete.id)

            assertEquals(0, response)
        }
    }

    @Test
    fun `flagging a report as deleted retriggers the paging query source`() {
        runBlockingTest {
            initData()
            val source = dao.loadReportHeaders("").toLiveData(1000)
            var value = source.getOrAwait()

            //+1 because of the separator
            assertEquals(UnitTestData.REPORTS.size + 1, value.size)

            val toDelete = UnitTestData.REPORTS.random()

            dao.flagReportDeleted(toDelete.id)

            value = source.getOrAwait()
            //+1 because of the separator
            assertEquals(UnitTestData.REPORTS.size, value.size)
        }
    }


    @Test
    fun `load report by id successfully`() {
        runBlockingTest {
            initData()
            val expected = UnitTestData.REPORTS.random()

            val report = dao.loadReportById(expected.id)
            assertEquals(expected, report)
        }
    }

    @Test
    fun `load report that does not exist`() {
        runBlockingTest {
            initData()

            val report = dao.loadReportById(UUID.randomUUID())

            assertNull(report)
        }
    }

    private suspend fun initData() {
        UnitTestData.BRANDS.union(UnitTestData.DELETED_BRANDS).forEach {
            db.brandDao.insertBrand(it)
        }
        UnitTestData.MODELS.union(UnitTestData.DELETED_MODELS).forEach {
            db.modelDao.insertModel(it)
        }
        UnitTestData.REPORTS.forEach {
            dao.saveReport(it)
        }
    }
}