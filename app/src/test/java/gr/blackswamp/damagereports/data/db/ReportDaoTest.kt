package gr.blackswamp.damagereports.data.db
//
//import android.database.sqlite.SQLiteConstraintException
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.test.InstrumentationRegistry
//import gr.blackswamp.damagereports.data.TestData
//import gr.blackswamp.damagereports.data.ToObservable
//import gr.blackswamp.damagereports.data.countWhere
//import gr.blackswamp.damagereports.data.db.dao.ReportDao
//import gr.blackswamp.damagereports.data.db.entities.ReportEntity
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import java.util.*
//
//class ReportDaoTest {
//    lateinit var db: AppDatabase
//    lateinit var dao: ReportDao
//
//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//
//    @Before
//    fun setUp() {
//        db = Room.inMemoryDatabaseBuilder(
//            InstrumentationRegistry.getContext(),
//            AppDatabase::class.java
//        )
//            .allowMainThreadQueries()
//            .build()
//
//        dao = db.reportDao
//    }
//
//    @After
//    fun tearDown() {
//        (db as RoomDatabase).close()
//    }
//
//    @Test
//    fun countRowsEmpty() {
//        dao.count()
//            .test()
//            .assertValue(0)
//            .assertComplete()
//    }
//
//    @Test
//    fun insertReportWithNotExistingBrandModel() {
//        val report = ReportEntity(UUID.randomUUID(), "hello", "world", TestData.MODELS[0].brand, TestData.MODELS[0].id)
//        dao.saveReport(report)
//            .test()
//            .assertNotComplete()
//            .assertError(SQLiteConstraintException::class.java)
//            .assertError { it.message!!.contains("FOREIGN KEY") }
//    }
//
//    @Test
//    fun insertReportWithNotExistingModel() {
//        val model = TestData.MODELS[0]
//        val brand = TestData.BRANDS.first { it.id == model.brand }
//        db.brandDao.saveBrand(brand).test()
//        val report = ReportEntity(UUID.randomUUID(), "hello", "world", TestData.MODELS[0].brand, TestData.MODELS[0].id)
//        dao.saveReport(report)
//            .test()
//            .assertNotComplete()
//            .assertError(SQLiteConstraintException::class.java)
//            .assertError { it.message!!.contains("FOREIGN KEY") }
//    }
//
//
//    @Test
//    fun insertReportWithNoProblem() {
//        val model = TestData.MODELS[0]
//        val brand = TestData.BRANDS.first { it.id == model.brand }
//        db.brandDao.saveBrand(brand).test()
//        db.modelDao.saveModel(model).test()
//        val report = ReportEntity(UUID.randomUUID(), "hello", "world", brand.id, model.id, Calendar.getInstance().time, Calendar.getInstance().time)
//
//        dao.saveReport(report)
//            .test()
//            .assertComplete()
//            .assertNoErrors()
//        assertEquals(1,
//            db.countWhere("reports", " name = '${report.name}' and description = '${report.description}' and brand='${report.brand}' and model = '${report.model}' and id = '${report.id}'"))
//    }
//
//    @Test
//    fun deleteReport() {
//        initData()
//        val deleted = TestData.REPORTS[3]
//        dao.deleteReportById(deleted.id)
//            .test()
//            .assertComplete()
//            .assertNoErrors()
//
//        assertEquals(0, db.countWhere("reports", " id='${deleted.id}'"))
//    }
//
//    @Test
//    fun searchReportWithNoArgs() {
//        initData()
//        val observer = dao.loadReportHeaders().ToObservable().test().assertNoErrors().assertValueCount(1)
//        val receivedIds = observer.values()[0].map { it.id }
//        assertEquals(TestData.REPORTS.size, TestData.REPORTS.count { receivedIds.contains(it.id) })
//    }
//
//    @Test
//    fun searchReportByName() {
//        initData()
//        val filter =
//            "Hello World" //this is on purpose 11 characters so that the random models cannot possibly contain it in their name
//        val expected = listOf(
//            ReportEntity(UUID.randomUUID(), "5${filter}1", "", TestData.BRANDS[7].id, TestData.MODELS.first { it.brand == TestData.BRANDS[7].id }.id)
//            , ReportEntity(UUID.randomUUID(), "2${filter}2", "", TestData.BRANDS[7].id, TestData.MODELS.first { it.brand == TestData.BRANDS[7].id }.id)
//            , ReportEntity(UUID.randomUUID(), "3${filter}3", "", TestData.BRANDS[7].id, TestData.MODELS.first { it.brand == TestData.BRANDS[7].id }.id)
//            , ReportEntity(UUID.randomUUID(), "1${filter}4", "", TestData.BRANDS[7].id, TestData.MODELS.first { it.brand == TestData.BRANDS[7].id }.id)
//        )
//        db.runInTransaction {
//            expected.forEach {
//                dao.saveReport(it).test()
//            }
//        }
//        val observer = dao.loadReportHeaders(filter).ToObservable().test().assertNoErrors().assertValueCount(1)
//        val receivedIds = observer.values()[0].map { it.id }
//        assertEquals(expected.size, expected.count { receivedIds.contains(it.id) })
//    }
//
//    @Test
//    fun searchReportByDescription() {
//        initData()
//        val filter =
//            "Hello World" //this is on purpose 11 characters so that the random models cannot possibly contain it in their name
//        val expected = listOf(
//            ReportEntity(UUID.randomUUID(), "", "5${filter}1", TestData.BRANDS[5].id, TestData.MODELS.first { it.brand == TestData.BRANDS[5].id }.id)
//            , ReportEntity(UUID.randomUUID(), "", "2${filter}2", TestData.BRANDS[5].id, TestData.MODELS.first { it.brand == TestData.BRANDS[5].id }.id)
//            , ReportEntity(UUID.randomUUID(), "", "3${filter}3", TestData.BRANDS[5].id, TestData.MODELS.first { it.brand == TestData.BRANDS[5].id }.id)
//            , ReportEntity(UUID.randomUUID(), "", "1${filter}4", TestData.BRANDS[5].id, TestData.MODELS.first { it.brand == TestData.BRANDS[5].id }.id)
//        )
//        db.runInTransaction {
//            expected.forEach {
//                dao.saveReport(it).test()
//            }
//        }
//        val observer = dao.loadReportHeaders(filter).ToObservable().test().assertNoErrors().assertValueCount(1)
//        val receivedIds = observer.values()[0].map { it.id }
//        assertEquals(expected.size, expected.count { receivedIds.contains(it.id) })
//    }
//
//    fun initData() {
//        db.runInTransaction {
//            TestData.BRANDS.union(TestData.DELETED_BRANDS).forEach {
//                db.brandDao.saveBrand(it).test().assertNoErrors()
//            }
//            TestData.MODELS.union(TestData.DELETED_MODELS).forEach {
//                db.modelDao.saveModel(it).test().assertNoErrors()
//            }
//            TestData.REPORTS.forEach {
//                dao.saveReport(it).test().assertNoErrors()
//            }
//        }
//
//    }
//}