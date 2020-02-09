package gr.blackswamp.damagereports.vms.make

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.data.Response
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.testing.AndroidKoinTest
import gr.blackswamp.core.testing.MainCoroutineScopeRule
import gr.blackswamp.core.testing.TestDispatchers
import gr.blackswamp.core.testing.getOrAwait
import gr.blackswamp.damagereports.UnitTestData
import gr.blackswamp.damagereports.data.repos.MakeRepository
import gr.blackswamp.damagereports.data.toData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.mockito.Mockito
import java.util.*

@ExperimentalCoroutinesApi
class BrandParentTest : AndroidKoinTest() {
    companion object {
        const val FILTER = "12j3kj1lk23mm.,asd"
        const val ERROR = "this is an error"
    }


    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    val repo = Mockito.mock(MakeRepository::class.java)

    override val modules = module {
        single<IDispatchers> { TestDispatchers }
        single { repo }
    }

    private lateinit var vm: MakeViewModelImpl

    private fun initVm(brandId: UUID?) {
        vm = MakeViewModelImpl(app, brandId, false)
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
        assertEquals(ERROR, vm.error.value)
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
        assertEquals(ERROR, vm.error.value)
        verify(repo).getBrands("", id)
    }

    @Test
    fun `when the filter changes the results change`() {
        initVm(null)
        assertNull(vm.brandFilter.value)
        assertNull(vm.brandList.value)
        val expected = UnitTestData.BRANDS.shuffled().take(30)
        whenever(repo.getBrands(FILTER, null)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))

        vm.newBrandFilter(FILTER, true)

        val values = vm.brandList.getOrAwait().toList()
        assertEquals(FILTER, vm.brandFilter.value)
        verify(repo).getBrands(FILTER, null)
        assertEquals(expected.size, values.size)
        assertEquals(expected.size, expected.map { it.id }.intersect(values.map { it.id }).size)
    }

    @Test
    fun `we ignore filter changes when we start with a brand`() {
        initVm(null)

        vm.newBrandFilter(FILTER, true)

        verifyNoMoreInteractions(repo)
    }

}