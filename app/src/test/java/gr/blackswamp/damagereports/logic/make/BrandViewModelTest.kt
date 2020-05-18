package gr.blackswamp.damagereports.logic.make

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
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
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.vms.BrandViewModelImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock

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


    private val repo = mock(BrandRepository::class.java)
    private val parent = mock(FragmentParent::class.java)

    override val modules = module {
        single<Dispatcher> { TestDispatcher }
        single { repo }
    }

    private lateinit var vm: BrandViewModelImpl

    @Test
    fun `initialization works`() {
        val brand = UnitTestData.BRANDS.random()
        initVm()
        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)
        whenever(repo.getBrands("")).thenReturn(Response.success(StaticDataSource.factory(listOf(brand).map { it.toData() })))

        vm.initialize()

        assertEquals(listOf(brand.toData()), vm.brandList.getOrAwait())
        assertEquals("", vm.brandFilter.value)
        verify(repo).getBrands("")
    }

    @Test
    fun `when there is an error while while initializing show an empty list and pop a message`() {
        initVm()
        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)
        whenever(repo.getBrands("")).thenReturn(Response.failure(ERROR))

        vm.initialize()

        assertEquals(0, vm.brandList.getOrAwait().count())
        verify(parent).showError(ERROR)
        verify(repo).getBrands("")
    }

    @Test
    fun `when the filter changes the results change`() {
        initVm()
        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)
        val expected = UnitTestData.BRANDS.shuffled().take(30).map { it.toData() }
        whenever(repo.getBrands(FILTER)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))

        vm.newFilter(FILTER, true)

        val values = vm.brandList.getOrAwait().toList()
        assertEquals(FILTER, vm.brandFilter.value)
        verify(repo).getBrands(FILTER)
        assertEquals(expected.size, values.size)
        assertEquals(expected.size, expected.map { it.id }.intersect(values.map { it.id }).size)
    }

    @Test
    fun `pressing add new creates an empty brand and shows it`() {
        initVm()

        vm.create()

        val value = vm.brand.getOrAwait()

        assertNotNull(value)
        assertEquals(EmptyUUID, value.id)
        assertEquals("", value.name)
    }

    @Test
    fun `pressing save with no new brand pressed shows an error`() {
        initVm()

        vm.brand.getOrAwait(time = 10, throwError = false) //just to make sure the transformation is observed
        vm.save("hello")

        verify(parent).showError(anyString())
        verify(parent).showLoading(false)
        verify(app).getString(R.string.error_new_brand_not_found)
        verifyNoMoreInteractions(app, repo)
    }

    @Test
    fun `pressing save with an empty named new brand shows an error`() {
        initVm()

        vm.create()
        vm.brand.getOrAwait()
        vm.save("")

        verify(parent).showError(anyString())
        verify(parent).showLoading(false)
        verify(app).getString(R.string.error_empty_brand_name)
        verifyNoMoreInteractions(app, repo)
    }

    @Test
    fun `pressing save with a new brand saves a new item`() {
        runBlocking {
            val name = "hello world"
            initVm()
            whenever(repo.newBrand(name)).thenReturn(Response.success())

            vm.create()
            vm.brand.getOrAwait()

            vm.save(name)
            vm.brand.getOrAwait(0, throwError = false)

            verify(parent, never()).showError(anyString())
            verify(parent).showLoading(false)
            assertNull(vm.brand.value)
            verify(repo).newBrand(name)
        }
    }

    @Test
    fun `pressing save with a new brand which has a problem shows the problem `() {
        runBlocking {
            val name = "hello world"
            initVm()
            whenever(repo.newBrand(name)).thenReturn(Response.failure(ERROR))
            vm.create()
            vm.brand.getOrAwait()

            vm.save(name)
            vm.brand.getOrAwait()


            verify(parent).showError(APP_STRING)
            verify(parent).showLoading(false)
            verify(app).getString(R.string.error_saving_brand)
            assertNotNull(vm.brand.value)
            verify(repo).newBrand(name)
        }
    }

    private fun initVm() {
        vm = BrandViewModelImpl(app, parent, false)
        reset(repo)
    }
}