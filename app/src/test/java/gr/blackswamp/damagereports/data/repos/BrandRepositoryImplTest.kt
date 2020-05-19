package gr.blackswamp.damagereports.data.repos

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import com.nhaarman.mockitokotlin2.*
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.testing.*
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
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
class BrandRepositoryImplTest : AndroidKoinTest() {
    companion object {
        private const val ERROR = "this is a brand error"
    }

    override val modules = module {
        single { db }
        single { prefs }
        single<Dispatcher> { TestDispatcher }
    }

    private val db = mock<AppDatabase>()
    private val prefs = mock<Preferences>()
    private lateinit var repo: BrandRepository
    private val bDao = mock<BrandDao>()
    private val mDao = mock<ModelDao>()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineScopeRule = MainCoroutineScopeRule()


    @Before
    override fun setUp() {
        super.setUp()
        reset(db, prefs)
        whenever(db.brandDao).thenReturn(bDao)
        whenever(db.modelDao).thenReturn(mDao)
        repo = BrandRepositoryImpl()
    }

    @Test
    fun `calling get brands calls the appropriate method`() {
        val filter = randomString(19)
        val brand = UnitTestData.BRANDS.random()
        whenever(bDao.loadBrands(filter)).thenReturn(StaticDataSource.factory(listOf(brand)))

        val response = repo.getBrands(filter)

        assertFalse(response.hasError)
        verify(bDao).loadBrands(filter)
        verifyNoMoreInteractions(bDao)
        assertEquals(listOf(brand.toData()), response.get.toLiveData(100).getOrAwait())
    }

    @Test
    fun `calling get brands and it causing an error returns the appropriate response`() {
        val error = SQLiteException("error with SQLite")
        whenever(bDao.loadBrands(any())).thenThrow(error)

        val response = repo.getBrands("")

        assertTrue(response.hasError)
        assertEquals(error, response.error)
    }

    @Test
    fun `calling new brand calls repository to add brand`() {
        runBlocking {
            val name = "hello world"
            whenever(bDao.insertBrand(any())).thenReturn(Unit)

            val response = repo.newBrand(name)

            assertFalse(response.hasError)
        }
    }


    @Test
    fun `calling new brand that already exists returns a failure`() {
        runBlocking {
            val name = "hello world"
            val error = "this is the error I will return"
            whenever(bDao.insertBrand(any())).thenThrow(SQLiteConstraintException(error))

            val response = repo.newBrand(name)

            assertTrue(response.hasError)
            assertEquals("Brand entity with name $name already exists", response.errorMessage)
        }
    }

    @Test
    fun `calling update brand calls repository to update the brand`() {
        runBlocking {
            val id = UUID.randomUUID()
            val name = "hello world"
            whenever(bDao.updateBrand(BrandEntity(id, name, false))).thenReturn(1)

            val response = repo.updateBrand(id, name)

            assertFalse(response.hasError)
        }
    }

    @Test
    fun `calling update brand that does not exist returns an error`() {
        runBlocking {
            val id = UUID.randomUUID()
            val name = "hello world"
            whenever(bDao.updateBrand(BrandEntity(id, name, false))).thenReturn(0)

            val response = repo.updateBrand(id, name)

            assertTrue(response.hasError)
            assertEquals("Brand entity $id could not be found", response.errorMessage)
        }
    }

    @Test
    fun `calling update brand that has the same name as another one returns an error`() {
        runBlocking {
            val id = UUID.randomUUID()
            val name = "hello world"
            whenever(bDao.updateBrand(BrandEntity(id, name, false))).thenThrow(SQLiteConstraintException("there is a problem"))

            val response = repo.updateBrand(id, name)

            assertTrue(response.hasError)
            assertEquals("Brand entity with $name already exists", response.errorMessage)
        }
    }

    @Test
    fun `calling flag brand as deleted calls the dao`() {
        runBlocking {
            val id = UUID.randomUUID()
            whenever(bDao.flagBrandDeleted(id)).thenReturn(1)

            val response = repo.deleteBrand(id)

            assertFalse(response.hasError)
        }
    }

    @Test
    fun `calling flag brand as deleted with no affected rows`() {
        runBlocking {
            val id = UUID.randomUUID()
            whenever(bDao.flagBrandDeleted(id)).thenReturn(0)
            whenever(app.getString(R.string.error_brand_not_found, id)).thenReturn(ERROR)

            val response = repo.deleteBrand(id)

            assertTrue(response.hasError)
            assertEquals(ERROR, response.errorMessage)
        }
    }

    @Test
    fun `calling flag brand as deleted with a dao error`() {
        runBlocking {
            val id = UUID.randomUUID()
            val error = SQLiteException("error with SQLite")
            whenever(bDao.flagBrandDeleted(id)).thenThrow(error)
            whenever(app.getString(R.string.error_deleting, error.message ?: error::class.java.name)).thenReturn(ERROR)

            val response = repo.deleteBrand(id)

            assertTrue(response.hasError)
            assertEquals(ERROR, response.errorMessage)
            assertEquals(error, response.error.cause)
        }
    }

}