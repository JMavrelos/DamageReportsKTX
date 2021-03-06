package gr.blackswamp.damagereports.data.db

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.room.Room
import androidx.room.paging.LimitOffsetDataSource
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import gr.blackswamp.core.db.countWhere
import gr.blackswamp.core.testing.getOrAwait
import gr.blackswamp.core.util.toDateString
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.TestData
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
            ApplicationProvider.getApplicationContext(),
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
            TestData.MODELS[0].brand,
            TestData.MODELS[0].id
        )
        runBlockingTest {
            var error: Throwable? = null
            try {
                dao.insertReport(report)
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
            val model = TestData.MODELS.random()
            val brand = TestData.BRANDS.filter { it.id == model.brand }.random()
            db.brandDao.insertBrand(brand)
            val report = ReportEntity(UUID.randomUUID(), "hello", "world", model.brand, model.id)

            var error: Throwable? = null
            try {
                dao.insertReport(report)
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
            val model = TestData.MODELS.random()
            val brand = TestData.BRANDS.filter { it.id == model.brand }.random()
            db.brandDao.insertBrand(brand)
            db.modelDao.insertModel(model)
            val report = ReportEntity(UUID.randomUUID(), "hello", "world", UUID.randomUUID(), model.id)

            var error: Throwable? = null
            try {
                dao.insertReport(report)
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
            val model = TestData.MODELS.random()
            val brand = TestData.BRANDS.filter { it.id == model.brand }.random()
            db.brandDao.insertBrand(brand)
            db.modelDao.insertModel(model)
            val report = ReportEntity(UUID.randomUUID(), "hello", "world", model.brand, model.id)

            dao.insertReport(report)

            val count = db.countWhere(
                "reports",
                " name = '${report.name}' and description = '${report.description}' and brand='${report.brand}' and model = '${report.model}' and id = '${report.id}'"
            )
            assertEquals(1, count)

        }
    }

    @Test
    fun `search a report with no arguments`() {
        runBlockingTest {
            initData()

            val result = (dao.loadReportHeaders("").create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(TestData.REPORTS.size, TestData.REPORTS.count { report -> result.map { it.id }.contains(report.id) })
        }
    }


    @Test
    fun `search report by name`() {
        runBlockingTest {
            initData()
            val filter = "Hello World" //this is on purpose 11 characters so that the random models cannot possibly contain it in their name
            val expected = listOf(
                ReportEntity(UUID.randomUUID(), "5${filter}1", "", TestData.BRANDS[7].id, TestData.MODELS.first { it.brand == TestData.BRANDS[7].id }.id)
                , ReportEntity(UUID.randomUUID(), "2${filter}2", "", TestData.BRANDS[7].id, TestData.MODELS.first { it.brand == TestData.BRANDS[7].id }.id)
                , ReportEntity(UUID.randomUUID(), "3${filter}3", "", TestData.BRANDS[7].id, TestData.MODELS.first { it.brand == TestData.BRANDS[7].id }.id)
                , ReportEntity(UUID.randomUUID(), "1${filter}4", "", TestData.BRANDS[7].id, TestData.MODELS.first { it.brand == TestData.BRANDS[7].id }.id)
            )
            expected.forEach {
                dao.insertReport(it)
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
                ReportEntity(UUID.randomUUID(), "", "5${filter}1", TestData.BRANDS[5].id, TestData.MODELS.first { it.brand == TestData.BRANDS[5].id }.id)
                , ReportEntity(UUID.randomUUID(), "", "2${filter}2", TestData.BRANDS[5].id, TestData.MODELS.first { it.brand == TestData.BRANDS[5].id }.id)
                , ReportEntity(UUID.randomUUID(), "", "3${filter}3", TestData.BRANDS[5].id, TestData.MODELS.first { it.brand == TestData.BRANDS[5].id }.id)
                , ReportEntity(UUID.randomUUID(), "", "1${filter}4", TestData.BRANDS[5].id, TestData.MODELS.first { it.brand == TestData.BRANDS[5].id }.id)
            )

            expected.forEach {
                dao.insertReport(it)
            }

            val result = (dao.loadReportHeaders(filter).create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(expected.size, expected.count { entity -> result.map { it.id }.contains(entity.id) })
        }
    }

    @Test
    fun `flagging report as deleted works`() {
        runBlockingTest {
            initData()
            val toDelete = TestData.REPORTS.random()

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
            val report = TestData.REPORTS.random()

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
            val report = TestData.REPORTS.random()

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
            val toDelete = TestData.REPORTS.random()

            val response = dao.unFlagReportDeleted(toDelete.id)

            assertEquals(0, response)
        }
    }

    @Test
    fun `flagging a report as deleted re-triggers the paging query source`() {
        runBlockingTest {
            initData()
            val source = dao.loadReportHeaders("").toLiveData(1000)
            var value = source.getOrAwait()
            val separators = TestData.REPORTS.map { it.created.toDateString() }.distinct().count()


            assertEquals(TestData.REPORTS.size + separators, value.size)

            val toDelete = TestData.REPORTS.random()

            dao.flagReportDeleted(toDelete.id)

            value = source.getOrAwait()

            val newSeparatorCount = TestData.REPORTS.toMutableList().apply {
                remove(toDelete)
            }.map { it.created.toDateString() }.distinct().count()

            assertEquals(TestData.REPORTS.size + newSeparatorCount - 1, value.size)
        }
    }


    @Test
    fun `load report by id successfully`() {
        runBlockingTest {
            initData()
            val expected = TestData.REPORTS.random()

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
        TestData.BRANDS.forEach {
            db.brandDao.insertBrand(it)
        }
        TestData.MODELS.forEach {
            db.modelDao.insertModel(it)
        }
        TestData.REPORTS.forEach {
            dao.insertReport(it)
        }
    }
}