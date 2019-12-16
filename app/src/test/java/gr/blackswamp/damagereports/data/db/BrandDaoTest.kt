package gr.blackswamp.damagereports.data.db

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import gr.blackswamp.core.count
import gr.blackswamp.core.countWhere
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class BrandDaoTest : KoinTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: BrandDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = db.brandDao
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun countRowsEmpty() {
        val count = runBlocking { dao.count() }
        assertEquals(0, count)
    }

    @Test
    fun insertNew() {
        val brand = UnitTestData.BRANDS[0]
        runBlocking { dao.saveBrand(brand) }
        assertEquals(1, db.count("brands"))
    }

    @Test
    fun countRowsFromInserted() {
        initBrands()
        val count = runBlocking { dao.count() }
        assertEquals(UnitTestData.BRANDS.size, count)
    }

    @Test
    fun updateBrand() {
        initBrands()
        val updated = UnitTestData.BRANDS[2].copy(name = "this is the new thang")
        runBlocking { dao.saveBrand(updated) }
        assertEquals(UnitTestData.BRANDS.size + UnitTestData.DELETED_BRANDS.size, db.count("brands"))
        assertEquals(1, db.countWhere("brands", " name = '${updated.name}'"))
    }

    @Test
    fun searchBrandsWithNoArgs() {
        initBrands()
        val entities = runBlocking { dao.searchBrands("") }
        assertEquals(UnitTestData.BRANDS.size, entities.size)
        assertEquals(UnitTestData.BRANDS.size, entities.count { l -> UnitTestData.BRANDS.any { it == l } })
    }

    @Test
    fun searchBrandsWithPaging() {
        initBrands()
        val entities = runBlocking { dao.searchBrands("", 10, 20) }
        assertEquals(UnitTestData.BRANDS.sortedBy { it.name }.subList(10, 30), entities)
    }

    @Test
    fun searchBrandsWithSearch() {
        initBrands()
        val filter =
            "Hello World" //this is on purpose 11 characters so that the random brands cannot possibly contain it in their name
        val expected = listOf(
            BrandEntity(UUID.randomUUID(), "5${filter}1", false)
            , BrandEntity(UUID.randomUUID(), "2${filter}2", false)
            , BrandEntity(UUID.randomUUID(), "3${filter}3", false)
            , BrandEntity(UUID.randomUUID(), "1${filter}4", false)
        )
        val entities =
            runBlocking {
                expected.forEach { dao.saveBrand(it) }
                dao.searchBrands(filter)
            }
        assertEquals(expected.sortedBy { it.name }, entities)
    }


    @Test
    fun searchBrandsWithSearchAndPaging() {
        initBrands()
        val filter =
            "Hello World" //this is on purpose 11 characters so that the random brands cannot possibly contain it in their name
        val searched = listOf(
            BrandEntity(UUID.randomUUID(), "5${filter}1", false)
            , BrandEntity(UUID.randomUUID(), "2${filter}2", false)
            , BrandEntity(UUID.randomUUID(), "3${filter}3", false)
            , BrandEntity(UUID.randomUUID(), "1${filter}4", false)
        )
        val entities = runBlocking {
            searched.forEach { dao.saveBrand(it) }
            dao.searchBrands(filter, 1, 2)
        }
        assertEquals(searched.sortedBy { it.name }.subList(1, 3), entities)
    }

    @Test
    fun deleteBrandWithNoModelsUnder() {
        initBrands()
        val deleted = UnitTestData.BRANDS[3].id
        runBlocking { dao.deleteBrandById(deleted) }
        assertEquals(0, db.countWhere("brands", " id = '$deleted'"))
    }

    @Test
    fun deleteBrandWithModelsUnderPropagates() {
        initBrands()
        runBlocking {
            UnitTestData.MODELS.forEach {
                db.modelDao.saveModel(it)
            }
        }

        val deleted = UnitTestData.BRANDS[3].id
        runBlocking { dao.deleteBrandById(deleted) }

        assertEquals(0, db.countWhere("brands", " id = '$deleted'"))
    }

    fun deleteBrandBeingUsedFails() {
        initBrands()
        val toDelete = UnitTestData.BRANDS[0]
        val model = UnitTestData.MODELS.first { it.brand == toDelete.id }
        runBlocking {
            db.modelDao.saveModel(model)
            db.reportDao.saveReport(ReportEntity(UUID.randomUUID(), "123", "123", toDelete.id, model.id))
            try {
                dao.deleteBrandById(toDelete.id)
            }catch (e:Exception) {
                assertEquals(SQLiteConstraintException::class,e.javaClass)
                assertTrue(e.message!!.contains("FOREIGN KEY"))
            }
        }
    }

    private fun initBrands() {
        db.runInTransaction {
            runBlocking {
                UnitTestData.BRANDS.union(UnitTestData.DELETED_BRANDS).forEach {
                    dao.saveBrand(it)
                    //we add the test so there will be a subscriber so the save will go through
                }
            }
        }
    }

}