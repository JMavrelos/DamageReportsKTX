package gr.blackswamp.damagereports.data.db
//
//import android.database.sqlite.SQLiteConstraintException
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.room.Room
//import androidx.test.InstrumentationRegistry
//import gr.blackswamp.damagereports.data.TestData
//import gr.blackswamp.damagereports.data.count
//import gr.blackswamp.damagereports.data.countWhere
//import gr.blackswamp.damagereports.data.db.dao.DamageDao
//import gr.blackswamp.damagereports.data.db.entities.DamageEntity
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import java.util.*
//
//class DamageDaoTest {
//    lateinit var db: AppDatabase
//    lateinit var dao: DamageDao
//
//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @Before
//    fun setUp() {
//        db = Room.inMemoryDatabaseBuilder(
//            InstrumentationRegistry.getContext()
//            , AppDatabase::class.java
//        )
//            .allowMainThreadQueries()
//            .build()
//        dao = db.damageDao
//    }
//
//    @Test
//    fun insertDamageWithoutReportFails() {
//        val insert = DamageEntity(UUID.randomUUID(), "failed", "failed", UUID.randomUUID())
//        dao.saveDamage(insert).test()
//            .assertNotComplete()
//            .assertError(SQLiteConstraintException::class.java)
//            .assertError { it.message!!.contains("FOREIGN KEY") }
//    }
//
//    @Test
//    fun insertDamage() {
//        val report = TestData.REPORTS[0]
//        db.brandDao.saveBrand(TestData.BRANDS.first { it.id == report.brand }).test()
//        db.modelDao.saveModel(TestData.MODELS.first { it.id == report.model }).test()
//        db.reportDao.saveReport(report).test()
//        val insert = DamageEntity(UUID.randomUUID(), "ok", "ok", report.id)
//        dao.saveDamage(insert).test()
//            .assertComplete()
//            .assertNoErrors()
//    }
//
//    @Test
//    fun deleteDamage() {
//        initData()
//        val deleted = TestData.DAMAGES[3].id
//        dao.deleteDamageById(deleted)
//            .test()
//            .assertNoErrors()
//            .assertComplete()
//        assertEquals(0, db.countWhere("damages", " id = '$deleted'"))
//        assertEquals(TestData.DAMAGES.size - 1, db.count("damages"))
//    }
//
//    @Test
//    fun loadDamagesForReport() {
//        initData()
//        val report = TestData.REPORTS[23]
//        dao.loadDamagesForReport(report.id)
//            .test()
//            .assertNoErrors()
//    }
//
//    @Test
//    fun deleteReportPropagatesToDamage() {
//
//    }
//
//
//    @After
//    fun tearDown() {
//        db.close()
//    }
//
//    private fun initData() {
//        db.runInTransaction {
//            TestData.BRANDS.union(TestData.DELETED_BRANDS).forEach {
//                db.brandDao.saveBrand(it).test().assertNoErrors()
//            }
//            TestData.MODELS.union(TestData.DELETED_MODELS).forEach {
//                db.modelDao.saveModel(it).test().assertNoErrors()
//            }
//            TestData.REPORTS.forEach {
//                db.reportDao.saveReport(it).test().assertNoErrors()
//            }
//            TestData.DAMAGES.forEach {
//                dao.saveDamage(it).test().assertNoErrors()
//            }
//        }
//    }
//}