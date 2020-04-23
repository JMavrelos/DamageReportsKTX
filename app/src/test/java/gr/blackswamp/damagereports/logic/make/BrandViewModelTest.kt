package gr.blackswamp.damagereports.logic.make

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.data.Response
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.testing.AndroidKoinTest
import gr.blackswamp.core.testing.MainCoroutineScopeRule
import gr.blackswamp.core.testing.TestDispatcher
import gr.blackswamp.core.testing.getOrAwait
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.repos.BrandRepository
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.logic.vms.BrandViewModelImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.mockito.Mockito
import java.util.*

@ExperimentalCoroutinesApi
class BrandViewModelTest : AndroidKoinTest() {
    companion object {
        const val FILTER = "12j3kj1lk23mm.,asd"
        const val ERROR = "this is an error"
    }

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private val repo = Mockito.mock(BrandRepository::class.java)

    override val modules = module {
        single<Dispatcher> { TestDispatcher }
        single { repo }
    }

    private lateinit var vm: BrandViewModelImpl

    private fun initVm(brandId: UUID?) {
        vm = BrandViewModelImpl(app, brandId, false)
        reset(repo)
    }

    @Test
    fun `load brands with no filter from the start with no pre set brand`() {
        initVm(null)
        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)

        whenever(repo.getBrands("", null)).thenReturn(Response.success(StaticDataSource.factory(listOf())))

        vm.initialize()


        assertEquals(0, vm.brandList.getOrAwait().count())
        assertEquals("", vm.brandFilter.value)
        verify(repo).getBrands("", null)
    }

    @Test
    fun `when there is an error while loading without a pre set brand show an empty list and pop a message`() {
        initVm(null)
        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)
        whenever(repo.getBrands("", null)).thenReturn(Response.failure(ERROR))

        vm.initialize()

        assertEquals(0, vm.brandList.getOrAwait().count())
//        assertEquals(ERROR, vm.error.value)
        verify(repo).getBrands("", null)
    }

    @Test
    fun `load brands with no filter from the start with a pre set brand`() {
        val brand = UnitTestData.BRANDS.random()
        initVm(brand.id)
        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)
        whenever(repo.getBrands("", brand.id)).thenReturn(Response.success(StaticDataSource.factory(listOf(brand))))

        vm.initialize()

        assertEquals(listOf(brand.toData()), vm.brandList.getOrAwait())
        assertEquals("", vm.brandFilter.value)
        verify(repo).getBrands("", brand.id)
    }

    @Test
    fun `when there is an error while loading without a set brand show an empty list and pop a message`() {
        val id = UUID.randomUUID()
        initVm(id)

        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)
        whenever(repo.getBrands("", id)).thenReturn(Response.failure(Throwable(ERROR)))

        vm.initialize()

        assertEquals(0, vm.brandList.getOrAwait().count())
//        assertEquals(ERROR, vm.error.value)
        verify(repo).getBrands("", id)
    }

    @Test
    fun `when the filter changes the results change`() {
        initVm(null)
        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)
        val expected = UnitTestData.BRANDS.shuffled().take(30)
        whenever(repo.getBrands(FILTER, null)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))

        vm.newFilter(FILTER, true)

        val values = vm.brandList.getOrAwait().toList()
        assertEquals(FILTER, vm.brandFilter.value)
        verify(repo).getBrands(FILTER, null)
        assertEquals(expected.size, values.size)
        assertEquals(expected.size, expected.map { it.id }.intersect(values.map { it.id }).size)
    }

    @Test
    fun `we ignore filter changes when we start with a brand`() {
        initVm(null)

        vm.newFilter(FILTER, true)

        verifyNoMoreInteractions(repo)
    }

    @Test
    fun `pressing add new creates an empty brand and shows it`() {
        initVm(null)

        vm.create()

        val value = vm.brand.getOrAwait()

        assertNotNull(value)
//        assertFalse(vm.loading.value!!)
        assertEquals(EmptyUUID, value.id)
        assertEquals("", value.name)
    }

    @Test
    fun `pressing save with no new brand pressed shows an error`() {
        initVm(null)

        vm.brand.getOrAwait(time = 10, throwError = false) //just to make sure the transformation is observed
        vm.save("hello")

//        assertNotNull(vm.error.value)
//        assertFalse(vm.loading.value!!)
        verify(app).getString(R.string.error_new_brand_not_found)
        verifyNoMoreInteractions(app, repo)
    }

    @Test
    fun `pressing save with an empty named new brand shows an error`() {
        initVm(null)

        vm.create()
        vm.brand.getOrAwait()
        vm.save("")

//        assertNotNull(vm.error.value)
//        assertFalse(vm.loading.value!!)
        verify(app).getString(R.string.error_empty_brand_name)
        verifyNoMoreInteractions(app, repo)
    }

    @Test
    fun `pressing save with a new brand saves a new item`() {
        runBlocking {
            val name = "hello world"
            initVm(null)
            whenever(repo.newBrand(name)).thenReturn(Response.success())

            vm.create()
            vm.brand.getOrAwait()

            vm.save(name)
            vm.brand.getOrAwait(0, throwError = false)

//            assertNull(vm.error.value)
//            assertFalse(vm.loading.value!!)
            assertNull(vm.brand.value)
            verify(repo).newBrand(name)
        }
    }

    @Test
    fun `pressing save with a new brand which has a problem shows the problem `() {
        runBlocking {
            val name = "hello world"
            initVm(null)
            whenever(repo.newBrand(name)).thenReturn(Response.failure(ERROR))
            vm.create()
            vm.brand.getOrAwait()

            vm.save(name)
            vm.brand.getOrAwait()

//            assertEquals(APP_STRING, vm.error.value)
            verify(app).getString(R.string.error_saving_brand)
//            assertFalse(vm.loading.value!!)
            assertNotNull(vm.brand.value)
            verify(repo).newBrand(name)
        }
    }
}