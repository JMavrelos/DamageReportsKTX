package gr.blackswamp.damagereports.data.db

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.paging.LimitOffsetDataSource
import androidx.test.platform.app.InstrumentationRegistry
import gr.blackswamp.core.count
import gr.blackswamp.core.countWhere
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.entities.ModelEntity
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class ModelDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: ModelDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        dao = db.modelDao
    }

    @After
    fun tearDown() {
        (db as RoomDatabase).close()
    }

    @Test
    fun `insert new model`() {
        runBlockingTest {
            val brand = UnitTestData.BRANDS[0]
            val model = UnitTestData.MODELS.first { it.brand == brand.id }
            db.brandDao.saveBrand(brand)
            dao.saveModel(model)

            assertEquals(1, db.count("models"))
        }
    }

    @Test
    fun `insert with invalid brand fails`() {
        runBlockingTest {
            val brand = UnitTestData.BRANDS[0]
            val model = UnitTestData.MODELS.first { it.brand == brand.id }
            var error: Throwable? = null
            try {
                dao.saveModel(model)
            } catch (t: Throwable) {
                println(t.message)
                error = t
            }
            assertTrue(error is SQLiteConstraintException)

            assertEquals(0, db.count("models"))
        }
    }

    @Test
    fun `update model`() {
        runBlockingTest {
            initModels()
            val updated = UnitTestData.MODELS[2].copy(name = "this is the new thang")
            dao.saveModel(updated)
            assertEquals(UnitTestData.MODELS.size + UnitTestData.DELETED_MODELS.size, db.count("models"))
            assertEquals(1, db.countWhere("models", " name = '${updated.name}'"))
        }
    }

    @Test
    fun `search models with no args`() {
        runBlockingTest {
            initModels()
            val loaded = (dao.loadModels("").create() as LimitOffsetDataSource).loadRange(0, 1000)
            assertEquals(UnitTestData.MODELS.size, loaded.count { l -> UnitTestData.MODELS.any { it == l } })
        }
    }

    @Test
    fun `search models with filter`() {
        runBlockingTest {
            initModels()
            val filter = "Hello World" //this is on purpose 11 characters so that the random models cannot possibly contain it in their name
            val expected = listOf(
                ModelEntity(UUID.randomUUID(), "5${filter}1", UnitTestData.BRANDS[70].id, false)
                , ModelEntity(UUID.randomUUID(), "2${filter}2", UnitTestData.BRANDS[70].id, false)
                , ModelEntity(UUID.randomUUID(), "3${filter}3", UnitTestData.BRANDS[70].id, false)
                , ModelEntity(UUID.randomUUID(), "1${filter}4", UnitTestData.BRANDS[70].id, false)
            )
            expected.forEach { dao.saveModel(it) }

            val loaded = (dao.loadModels(filter).create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(expected.sortedBy { it.name }, loaded)
        }
    }

    @Test
    fun `delete model`() {
        runBlockingTest {
            initModels()
            val deleted = UnitTestData.MODELS[3].id
            dao.deleteModelById(deleted)

            assertEquals(0, db.countWhere("models", " id = '$deleted'"))
        }
    }

    @Test
    fun `delete model used fails`() {
        runBlockingTest {
            initModels()
            val toDelete = UnitTestData.MODELS[3]
            db.reportDao.saveReport(ReportEntity(UUID.randomUUID(), "123", "123", toDelete.brand, toDelete.id))
            val expected = db.count("models")

            var error: Throwable? = null
            try {
                dao.deleteModelById(toDelete.id)
            } catch (t: Throwable) {
                println(t)
                error = t
            }
            assertTrue(error is SQLiteConstraintException)
            assertEquals(expected, db.count("models"))
        }
    }

    private suspend fun initModels() {
        UnitTestData.BRANDS.union(UnitTestData.DELETED_BRANDS).forEach {
            db.brandDao.saveBrand(it)
        }
        UnitTestData.MODELS.union(UnitTestData.DELETED_MODELS).forEach {
            dao.saveModel(it)
        }
    }

}
