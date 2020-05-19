package gr.blackswamp.damagereports.logic.vms

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
import gr.blackswamp.damagereports.logic.commands.BrandCommand
import gr.blackswamp.damagereports.logic.interfaces.BrandViewModel
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
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

    private lateinit var vm: BrandViewModel
    private val vmImpl get() = vm as BrandViewModelImpl

    @Before
    override fun setUp() {
        super.setUp()
        vm = BrandViewModelImpl(app, parent, false)
        reset(repo, parent)
    }


    @Test
    fun `initialization works`() {
        val brand = UnitTestData.BRANDS.random()
        assertNull(vmImpl.brandFilter.value)
        assertNull(vm.brandList.value)
        whenever(repo.getBrands("")).thenReturn(Response.success(StaticDataSource.factory(listOf(brand).map { it.toData() })))

        vmImpl.initialize()

        assertEquals(listOf(brand.toData()), vm.brandList.getOrAwait())
        assertEquals("", vmImpl.brandFilter.value)
        verify(repo).getBrands("")
    }

    @Test
    fun `when there is an error while while initializing show an empty list and pop a message`() {
        assertNull(vmImpl.brandFilter.value)
        assertNull(vm.brandList.value)
        whenever(repo.getBrands("")).thenReturn(Response.failure(ERROR))

        vmImpl.initialize()

        assertEquals(0, vm.brandList.getOrAwait().count())
        verify(parent).showError(ERROR)
        verify(repo).getBrands("")
    }

    @Test
    fun `when the filter changes the results change`() {
        assertNull(vmImpl.brandFilter.value)
        assertNull(vm.brandList.value)
        val expected = UnitTestData.BRANDS.shuffled().take(30).map { it.toData() }
        whenever(repo.getBrands(FILTER)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))

        vm.newFilter(FILTER, true)

        val values = vm.brandList.getOrAwait().toList()
        assertEquals(FILTER, vmImpl.brandFilter.value)
        verify(repo).getBrands(FILTER)
        verify(parent).hideKeyboard()
        assertEquals(expected.size, values.size)
        assertEquals(expected.size, expected.map { it.id }.intersect(values.map { it.id }).size)
    }

    @Test
    fun `changing the filter that throws an error clears the results `() {
        runBlocking {
            whenever(repo.getBrands(anyString())).thenReturn(Response.failure(ERROR))

            vm.newFilter(FILTER, false)

            val list = vm.brandList.getOrAwait() //to make sure invokes happen
            verify(repo).getBrands(FILTER)
            verify(parent, never()).hideKeyboard()
            verify(parent).showError(ERROR)

            assertEquals(0, list.size)
        }
    }

    @Test
    fun `pressing add new creates an empty brand and shows it`() {

        vm.create()

        val value = vm.brand.getOrAwait()
        assertNotNull(value)
        assertEquals(EmptyUUID, value.id)
        assertEquals("", value.name)
    }

    @Test
    fun `pressing save with no new brand pressed shows an error`() {

        vm.brand.getOrAwait(time = 10, throwError = false) //just to make sure the transformation is observed
        vm.save("hello")

        verify(parent).showError(anyString())
        verify(parent).showLoading(false)
        verify(app).getString(R.string.error_new_brand_not_found)
        verifyNoMoreInteractions(app, repo)
    }

    @Test
    fun `pressing save with an empty named new brand shows an error`() {

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

    @Test
    fun `pressing edit loads the edited brand to memory and displays it for editing`() {
        runBlocking {

            val brand = UnitTestData.BRANDS.random()
            val id = brand.id
            whenever(repo.getBrand(id)).thenReturn(Response.success(brand.toData()))

            vm.edit(id)

            verify(repo).getBrand(id)
            verify(parent).showLoading(false)
            verify(parent, never()).showError(anyString())
            assertEquals(brand.toData(), vm.brand.getOrAwait())
        }
    }

    @Test
    fun `pressing edit loads the edited brand to memory but there is an error while loading`() {
        runBlocking {
            val brand = UnitTestData.BRANDS.random()
            val id = brand.id
            whenever(repo.getBrand(id)).thenReturn(Response.failure(ERROR))

            vm.edit(id)

            verify(repo).getBrand(id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertNull(vm.brand.getOrAwait(200L, throwError = false))
        }
    }

    @Test
    fun `pressing cancel with a new brand clears everything`() {
        vm.create()
        assertNotNull(vm.brand.getOrAwait())

        vm.cancel()


        verifyNoMoreInteractions(repo)
        assertNull(vm.brand.getOrAwait())
    }

    @Test
    fun `pressing cancel with an edited brand clears everything`() {
        runBlocking {
            val brand = UnitTestData.BRANDS.random()
            val id = brand.id
            whenever(repo.getBrand(id)).thenReturn(Response.success(brand.toData()))
            vm.edit(id)
            assertEquals(brand.toData(), vm.brand.getOrAwait())
            reset(repo)

            vm.cancel()

            verifyNoMoreInteractions(repo)
            assertNull(vm.brand.getOrAwait())
        }
    }

    @Test
    fun `deleting a brand already shows an undo message`() {
        runBlocking {
            val deleted = UnitTestData.BRANDS.random()
            whenever(repo.deleteBrand(deleted.id)).thenReturn(Response.success())

            vm.delete(deleted.id)

            verify(repo).deleteBrand(deleted.id)
            verify(parent).showLoading(false)
            verify(parent, never()).showError(anyString())
            assertTrue(vm.showUndo.getOrAwait())
            assertEquals(deleted.id, vmImpl.lastDeleted.value)
        }
    }

    @Test
    fun `deleting a brand with an error`() {
        runBlocking {
            val id = UnitTestData.BRANDS.random()
            whenever(repo.deleteBrand(id.id)).thenReturn(Response.failure(ERROR))

            vm.delete(id.id)

            verify(repo).deleteBrand(id.id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertEquals(null, vm.showUndo.getOrAwait(time = 200L, throwError = false))
            assertNull(vmImpl.lastDeleted.value)
        }
    }

    @Test
    fun `un-deleting a brand shows it again`() {
        runBlocking {
            val id = UnitTestData.BRANDS.random().id
            vmImpl.lastDeleted.postValue(id)
            vm.showUndo.getOrAwait()
            whenever(repo.restoreBrand(id)).thenReturn(Response.success())

            vm.undoLastDelete()

            verify(repo).restoreBrand(id)
            verify(parent).showLoading(false)
            verify(parent, never()).showError(anyString())
            assertNull(vmImpl.lastDeleted.value)
        }
    }

    @Test
    fun `un-deleting a brand with an error`() {
        runBlocking {
            val id = UnitTestData.BRANDS.random().id
            vmImpl.lastDeleted.postValue(id)
            vm.showUndo.getOrAwait()
            whenever(repo.restoreBrand(id)).thenReturn(Response.failure(ERROR))

            vm.undoLastDelete()

            verify(repo).restoreBrand(id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertNotNull(vmImpl.lastDeleted.value)
        }
    }

    @Test
    fun `select a brand`() {
        runBlocking {
            val brand = UnitTestData.BRANDS.random()
            val id = brand.id
            whenever(repo.getBrand(id)).thenReturn(Response.success(brand.toData()))

            vm.select(id)

            verify(repo).getBrand(id)
            verify(parent).showLoading(false)
            verify(parent, never()).showError(anyString())
            assertTrue(vm.command.value is BrandCommand.ShowModelSelect)
            assertEquals(brand.toData(), (vm.command.value as BrandCommand.ShowModelSelect).brand)
        }
    }

    @Test
    fun `select a brand that cannot be loaded`() {
        runBlocking {
            val brand = UnitTestData.BRANDS.random()
            val id = brand.id
            whenever(repo.getBrand(id)).thenReturn(Response.failure(ERROR))

            vm.select(id)

            verify(repo).getBrand(id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertNull(vm.command.getOrAwait(time = 200, throwError = false))
        }
    }

    @Test
    fun `refreshing triggers the load again`() {
        val expected = UnitTestData.BRANDS.shuffled().take(30).map { it.toData() }
        whenever(repo.getBrands(FILTER)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))
        vm.newFilter(FILTER, true)
        vm.brandList.getOrAwait() //to trigger the change

        vm.refresh()
        vm.brandList.getOrAwait() //to trigger the change

        verify(repo, times(2)).getBrands(FILTER)
    }
}