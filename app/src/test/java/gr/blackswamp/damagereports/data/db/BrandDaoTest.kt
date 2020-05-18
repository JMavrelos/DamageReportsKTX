package gr.blackswamp.damagereports.data.db

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.paging.LimitOffsetDataSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import gr.blackswamp.core.db.count
import gr.blackswamp.core.db.countWhere
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestApp::class)
class BrandDaoTest {
    private lateinit var db: AppDatabaseImpl
    private lateinit var dao: BrandDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, AppDatabaseImpl::class.java)
            .allowMainThreadQueries()
            .build()

        dao = db.brandDao
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `count when rows are empty`() {
        val count = runBlocking { dao.count() }
        assertEquals(0, count)
    }

    @Test
    fun `insert new brand`() {
        val brand = UnitTestData.BRANDS[0]
        runBlocking { dao.insertBrand(brand) }
        assertEquals(1, db.count("brands"))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `insert new brand that already exists`() {
        runBlocking {
            initBrands()
            val brand = UnitTestData.BRANDS[0]
            dao.insertBrand(brand)
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `insert brand with the same name as existing one`() {
        runBlocking {
            initBrands()
            val brand = UnitTestData.BRANDS[0].copy(UUID.randomUUID())
            dao.insertBrand(brand)
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `insert brand with the same name as existing one but different case`() {
        runBlocking {
            initBrands()
            val name = UnitTestData.BRANDS[0].name.toUpperCase()
            val brand = UnitTestData.BRANDS[0].copy(UUID.randomUUID(), name = name)
            dao.insertBrand(brand)
        }
    }

    @Test
    fun `count rows after inserted`() {
        runBlockingTest {
            initBrands()
            val count = dao.count()
            assertEquals(UnitTestData.BRANDS.size, count)
        }
    }

    @Test
    fun `update brand`() {
        runBlockingTest {
            initBrands()
            val updated = UnitTestData.BRANDS[2].copy(name = "this is the new thang")
            val affected = dao.updateBrand(updated)
            assertEquals(UnitTestData.BRANDS.size + UnitTestData.DELETED_BRANDS.size, db.count("brands"))
            assertEquals(1, db.countWhere("brands", " name = '${updated.name}'"))
            assertEquals(1, affected)
        }
    }

    @Test
    fun `update brand that does not exist`() {
        runBlocking {
            initBrands()
            val brand = BrandEntity(UUID.randomUUID(), "hello", false)
            val affected = dao.updateBrand(brand)
            assertEquals(0, affected)
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `update brand with the same name as another brand`() {
        runBlocking {
            initBrands()
            val brand = BrandEntity(UnitTestData.BRANDS[0].id, UnitTestData.BRANDS[1].name, false)
            dao.updateBrand(brand)
        }
    }

    @Test
    fun `search brands with no args`() {
        runBlockingTest {
            initBrands()
            val entities = (dao.loadBrands("").create() as LimitOffsetDataSource).loadRange(0, 1000)
            assertEquals(UnitTestData.BRANDS.size, entities.size)
            assertEquals(UnitTestData.BRANDS.size, entities.count { l -> UnitTestData.BRANDS.any { it == l } })
        }
    }

    @Test
    fun `search brands with a filter`() {
        runBlockingTest {
            initBrands()
            val filter = "Hello World" //this is on purpose 11 characters so that the random brands cannot possibly contain it in their name
            val expected = listOf(
                BrandEntity(UUID.randomUUID(), "5${filter}1", false)
                , BrandEntity(UUID.randomUUID(), "2${filter}2", false)
                , BrandEntity(UUID.randomUUID(), "3${filter}3", false)
                , BrandEntity(UUID.randomUUID(), "1${filter}4", false)
            )
            expected.forEach { dao.insertBrand(it) }

            val entities = (dao.loadBrands(filter).create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(expected.sortedBy { it.name }, entities)
        }
    }

    @Test
    fun `when searching brands with a filter then we do not return deleted ones`() {
        runBlockingTest {
            initBrands()
            val filter = "Hello World" //this is on purpose 11 characters so that the random brands cannot possibly contain it in their name
            val unexpected = listOf(
                BrandEntity(UUID.randomUUID(), "3${filter}3", true)
                , BrandEntity(UUID.randomUUID(), "1${filter}4", true)
            )
            val expected = listOf(
                BrandEntity(UUID.randomUUID(), "5${filter}1", false)
                , BrandEntity(UUID.randomUUID(), "2${filter}2", false)
            )
            expected.forEach { dao.insertBrand(it) }
            unexpected.forEach { dao.insertBrand(it) }

            val entities = (dao.loadBrands(filter).create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(expected.sortedBy { it.name }, entities)
        }
    }

    @Test
    fun `delete brand with no models under`() {
        runBlockingTest {
            initBrands()
            val deleted = UnitTestData.BRANDS[3].id
            dao.deleteBrandById(deleted)
            assertEquals(0, db.countWhere("brands", " id = '$deleted'"))
        }
    }

    @Test
    fun `delete brand with models under propagates`() {
        runBlockingTest {
            initBrands()
            UnitTestData.MODELS.forEach {
                db.modelDao.insertModel(it)
            }
            val deleted = UnitTestData.BRANDS[3].id

            dao.deleteBrandById(deleted)

            assertEquals(0, db.countWhere("brands", " id = '$deleted'"))
        }
    }

    @Test
    fun `delete brand being used fails`() {
        runBlockingTest {
            initBrands()
            val toDelete = UnitTestData.BRANDS[0]
            val model = UnitTestData.MODELS.first { it.brand == toDelete.id }
            db.modelDao.insertModel(model)
            db.reportDao.saveReport(ReportEntity(UUID.randomUUID(), "123", "123", toDelete.id, model.id))
            var error: Throwable? = null
            val expected = db.count("brands")
            try {
                dao.deleteBrandById(toDelete.id)
            } catch (e: Throwable) {
                error = e

            }
            assertTrue(error is SQLiteConstraintException)
            assertEquals(expected, db.count("brands"))
        }
    }

    @Test
    fun `load brand by id successfully`() {
        runBlockingTest {
            initBrands()
            val expected = UnitTestData.BRANDS.random()

            val brand = dao.loadBrandById(expected.id)

            assertEquals(expected, brand)
        }
    }

    @Test
    fun `load brand that does not exist`() {
        runBlockingTest {
            initBrands()

            val brand = dao.loadBrandById(UUID.randomUUID())

            Assert.assertNull(brand)
        }
    }


    private suspend fun initBrands() {
        UnitTestData.BRANDS.union(UnitTestData.DELETED_BRANDS).forEach {
            dao.insertBrand(it)
        }

    }
}
