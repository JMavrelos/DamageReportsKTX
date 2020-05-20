package gr.blackswamp.damagereports.data.db

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.paging.LimitOffsetDataSource
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import gr.blackswamp.core.db.count
import gr.blackswamp.core.db.countWhere
import gr.blackswamp.core.testing.getOrAwait
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.entities.ModelEntity
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
class ModelDaoTest {
    private lateinit var db: AppDatabaseImpl
    private lateinit var dao: ModelDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabaseImpl::class.java
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
            val brand = UnitTestData.BRANDS.random()
            val model = UnitTestData.MODELS.filter { it.brand == brand.id }.random()
            db.brandDao.insertBrand(brand)
            dao.insertModel(model)

            assertEquals(1, db.count("models"))
        }
    }

    @Test
    fun `insert with invalid brand fails`() {
        runBlockingTest {
            val brand = UnitTestData.BRANDS.random()
            val model = UnitTestData.MODELS.first { it.brand == brand.id }
            var error: Throwable? = null
            try {
                dao.insertModel(model)
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
            val updated = UnitTestData.MODELS.random().copy(name = "this is the new thing")
            dao.updateModel(updated)
            assertEquals(UnitTestData.MODELS.size, db.count("models"))
            assertEquals(1, db.countWhere("models", " name = '${updated.name}'"))
        }
    }

    @Test
    fun `search models with no args`() {
        runBlockingTest {
            initModels()

            val loaded = (dao.loadModels(UnitTestData.MODELS[2].id, "").create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(UnitTestData.MODELS.count { it.brand == UnitTestData.MODELS[2].id }, loaded.count { l -> UnitTestData.MODELS.any { it == l } })
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
            expected.forEach { dao.insertModel(it) }

            val loaded = (dao.loadModels(UnitTestData.BRANDS[70].id, filter).create() as LimitOffsetDataSource).loadRange(0, 1000)

            assertEquals(expected.sortedBy { it.name }, loaded)
        }
    }

    @Test
    fun `delete model`() {
        runBlockingTest {
            initModels()
            val deleted = UnitTestData.MODELS.random().id
            dao.deleteModelById(deleted)

            assertEquals(0, db.countWhere("models", " id = '$deleted'"))
        }
    }

    @Test
    fun `delete model used fails`() {
        runBlockingTest {
            initModels()
            val toDelete = UnitTestData.MODELS.random()
            db.reportDao.insertReport(ReportEntity(UUID.randomUUID(), "123", "123", toDelete.brand, toDelete.id))
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

    @Test
    fun `load model by id successfully`() {
        runBlockingTest {
            initModels()
            val expected = UnitTestData.MODELS.random()

            val model = dao.loadModelById(expected.id)

            assertEquals(expected, model)
        }
    }

    @Test
    fun `load model that does not exist`() {
        runBlockingTest {
            initModels()

            val model = dao.loadModelById(UUID.randomUUID())

            assertNull(model)
        }
    }

    @Test
    fun `flagging model as deleted works`() {
        runBlockingTest {
            initModels()
            val toDelete = UnitTestData.MODELS.random()

            val response = dao.flagModelDeleted(toDelete.id)

            assertEquals(1, response)
            val count = db.countWhere("models", " id = '${toDelete.id}' and deleted = 1")
            assertEquals(1, count)
        }
    }

    @Test
    fun `flagging an already deleted model returns that nothing is affected`() {
        runBlockingTest {
            initModels()
            val brand = UnitTestData.MODELS.random()

            dao.flagModelDeleted(brand.id)

            var count = db.countWhere("models", " id = '${brand.id}' and deleted = 1")
            assertEquals(1, count)

            val response = dao.flagModelDeleted(brand.id)
            assertEquals(0, response)
            count = db.countWhere("models", " id = '${brand.id}' and deleted = 1")
            assertEquals(1, count)
        }
    }

    @Test
    fun `unDeleting a model works`() {
        runBlockingTest {
            initModels()

            val model = UnitTestData.MODELS.random()

            dao.flagModelDeleted(model.id)

            var count = db.countWhere("models", " id = '${model.id}' and deleted = 1")
            assertEquals(1, count)

            val response = dao.unFlagModelDeleted(model.id)
            assertEquals(1, response)
            count = db.countWhere("models", " id = '${model.id}' and deleted = 0")
            assertEquals(1, count)
        }
    }

    @Test
    fun `unDeleting a model that is not deleted returns that nothing is affected`() {
        runBlockingTest {

            initModels()
            val toDelete = UnitTestData.MODELS.random()

            val response = dao.unFlagModelDeleted(toDelete.id)

            assertEquals(0, response)
        }
    }

    @Test
    fun `flagging a model as deleted re-triggers the paging query source`() {
        runBlockingTest {
            initModels()
            val brandId = UnitTestData.BRANDS.random().id
            val models = UnitTestData.MODELS.filter { it.brand == brandId }

            val source = dao.loadModels(brandId, "").toLiveData(1000)
            var value = source.getOrAwait()

            assertEquals(models.size, value.size)

            val toDelete = UnitTestData.MODELS.filter { it.brand == brandId }.random()

            dao.flagModelDeleted(toDelete.id)

            value = source.getOrAwait()
            assertEquals(models.size - 1, value.size)
        }
    }

    private suspend fun initModels() {
        UnitTestData.BRANDS.forEach {
            db.brandDao.insertBrand(it)
        }
        UnitTestData.MODELS.forEach {
            dao.insertModel(it)
        }
    }
}
