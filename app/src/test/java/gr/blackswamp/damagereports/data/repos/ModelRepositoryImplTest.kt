package gr.blackswamp.damagereports.data.repos

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import com.nhaarman.mockitokotlin2.*
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.testing.*
import gr.blackswamp.core.util.RandomUUID
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.entities.ModelEntity
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.data.toData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import java.util.*

@ExperimentalCoroutinesApi
class ModelRepositoryImplTest : KoinUnitTest() {
    companion object {
        private const val ERROR = "this is a model error"
    }

    override val modules = module {
        single { db }
        single { prefs }
        single<Dispatcher> { TestDispatcher }
    }

    private val db = mock<AppDatabase>()
    private val prefs = mock<Preferences>()
    private lateinit var repo: ModelRepository
    private val mDao = mock<ModelDao>()
    private val brandId = RandomUUID

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Suppress("SpellCheckingInspection")
    @get:Rule
    var mainCoroutineScopeRule = MainCoroutineScopeRule()


    @Before
    override fun setUp() {
        super.setUp()
        reset(db, prefs)
        whenever(db.modelDao).thenReturn(mDao)
        repo = ModelRepositoryImpl()
    }

    @Test
    fun `calling get models calls the appropriate method`() {
        val filter = randomString(19)
        val model = UnitTestData.MODELS.random()
        whenever(mDao.loadModels(brandId, filter)).thenReturn(StaticDataSource.factory(listOf(model)))

        val response = repo.getModels(brandId, filter)

        assertFalse(response.hasError)
        verify(mDao).loadModels(brandId, filter)
        verifyNoMoreInteractions(mDao)
        assertEquals(listOf(model.toData()), response.get.toLiveData(100).getOrAwait())
    }

    @Test
    fun `calling get models and it causing an error returns the appropriate response`() {
        val error = SQLiteException("error with SQLite")
        whenever(mDao.loadModels(eq(brandId), any())).thenThrow(error)

        val response = repo.getModels(brandId, "")

        assertTrue(response.hasError)
        assertEquals(error, response.error)
    }

    @Test
    fun `calling new model calls repository to add model`() {
        runBlocking {
            val name = "hello world"
            whenever(mDao.insertModel(any())).thenReturn(Unit)

            val response = repo.newModel(name, brandId)

            assertFalse(response.hasError)
        }
    }


    @Test
    fun `calling new model that already exists returns a failure`() {
        runBlocking {
            val name = "hello world"
            val error = "this is the error I will return"
            whenever(mDao.insertModel(any())).thenThrow(SQLiteConstraintException(error))

            val response = repo.newModel(name, brandId)

            assertTrue(response.hasError)
            assertEquals("Model entity with name $name already exists", response.errorMessage)
        }
    }

    @Test
    fun `calling update model calls repository to update the model`() {
        runBlocking {
            val id = UUID.randomUUID()
            val name = "hello world"
            whenever(mDao.updateModel(ModelEntity(id, name, brandId, false))).thenReturn(1)

            val response = repo.updateModel(id, brandId, name)

            assertFalse(response.hasError)
        }
    }

    @Test
    fun `calling update model that does not exist returns an error`() {
        runBlocking {
            val id = UUID.randomUUID()
            val name = "hello world"
            whenever(mDao.updateModel(ModelEntity(id, name, brandId, false))).thenReturn(0)

            val response = repo.updateModel(id, brandId, name)

            assertTrue(response.hasError)
            assertEquals("Model entity $id could not be found", response.errorMessage)
        }
    }

    @Test
    fun `calling update model that has the same name as another one returns an error`() {
        runBlocking {
            val id = UUID.randomUUID()
            val name = "hello world"
            whenever(mDao.updateModel(ModelEntity(id, name, brandId, false))).thenThrow(SQLiteConstraintException("there is a problem"))

            val response = repo.updateModel(id, brandId, name)

            assertTrue(response.hasError)
            assertEquals("Model entity with $name already exists", response.errorMessage)
        }
    }

    @Test
    fun `calling flag model as deleted calls the dao`() {
        runBlocking {
            val id = UUID.randomUUID()
            whenever(mDao.flagModelDeleted(id)).thenReturn(1)

            val response = repo.deleteModel(id)

            assertFalse(response.hasError)
        }
    }

    @Test
    fun `calling flag model as deleted with no affected rows`() {
        runBlocking {
            val id = UUID.randomUUID()
            whenever(mDao.flagModelDeleted(id)).thenReturn(0)
            whenever(app.getString(R.string.error_model_not_found, id)).thenReturn(ERROR)

            val response = repo.deleteModel(id)

            assertTrue(response.hasError)
            assertEquals(ERROR, response.errorMessage)
        }
    }

    @Test
    fun `calling flag model as deleted with a dao error`() {
        runBlocking {
            val id = UUID.randomUUID()
            val error = SQLiteException("error with SQLite")
            whenever(mDao.flagModelDeleted(id)).thenThrow(error)
            whenever(app.getString(R.string.error_deleting, error.message ?: error::class.java.name)).thenReturn(ERROR)

            val response = repo.deleteModel(id)

            assertTrue(response.hasError)
            assertEquals(ERROR, response.errorMessage)
            assertEquals(error, response.error.cause)
        }
    }

}