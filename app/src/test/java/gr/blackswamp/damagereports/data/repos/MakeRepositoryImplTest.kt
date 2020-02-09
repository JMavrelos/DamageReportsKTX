package gr.blackswamp.damagereports.data.repos

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import com.nhaarman.mockitokotlin2.*
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.testing.*
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.prefs.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import java.util.*

@ExperimentalCoroutinesApi
class MakeRepositoryImplTest : AndroidKoinTest() {
    override val modules = module {
        single { db }
        single { prefs }
        single<IDispatchers> { TestDispatchers }
    }

    private val db = mock<AppDatabase>()
    private val prefs = mock<Preferences>()
    private lateinit var repo: MakeRepository
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
        repo = MakeRepositoryImpl()
    }

    @Test
    fun `calling get brands with a null brand calls the appropriate method`() {
        val id = UUID.randomUUID()
        val brand = UnitTestData.BRANDS.random()
        whenever(bDao.loadBrandFactoryById(id)).thenReturn(StaticDataSource.factory(listOf(brand)))

        val response = repo.getBrands("askjdlasd", id)

        assertFalse(response.hasError)

        verify(bDao).loadBrandFactoryById(id)
        verifyNoMoreInteractions(bDao)
        assertEquals(listOf(brand), response.get.toLiveData(100).getOrAwait())
    }

    @Test
    fun `calling get brands with a non-null brand calls the appropriate method`() {
        val filter = randomString(19)
        val brands = UnitTestData.BRANDS.shuffled().take(50)
        whenever(bDao.loadBrands(filter)).thenReturn(StaticDataSource.factory(brands))

        val response = repo.getBrands(filter, null)

        assertFalse(response.hasError)

        verify(bDao).loadBrands(filter)
        verifyNoMoreInteractions(bDao)
        assertEquals(brands, response.get.toLiveData(100).getOrAwait())
    }

    @Test
    fun `calling get brands and it causing an error returns the appropriate response`() {
        val error = SQLiteException("error with sqlite")
        whenever(bDao.loadBrands(any())).thenThrow(error)

        val response = repo.getBrands("", null)

        assertTrue(response.hasError)
        assertEquals(error, response.error)
    }

}