package gr.blackswamp.damagereports.logic.vms

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.data.Response
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.testing.KoinUnitTest
import gr.blackswamp.core.testing.MainCoroutineScopeRule
import gr.blackswamp.core.testing.TestDispatcher
import gr.blackswamp.core.testing.getOrAwait
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.TestData
import gr.blackswamp.damagereports.data.repos.ModelRepository
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.logic.commands.ModelCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ModelViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class ModelViewModelTest : KoinUnitTest() {
    companion object {
        const val FILTER = "12j3kj1lk23mm.,asd"
        const val ERROR = "this is an error"
    }

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private val repo = Mockito.mock(ModelRepository::class.java)
    private val parent = Mockito.mock(FragmentParent::class.java)

    override val modules = module {
        single<Dispatcher> { TestDispatcher }
        single { repo }
    }

    private lateinit var vm: ModelViewModel
    private val vmImpl get() = vm as ModelViewModelImpl
    private val brandId = TestData.BRANDS.random().id

    @Before
    override fun setUp() {
        super.setUp()
        vm = ModelViewModelImpl(app, parent, brandId, false)
        reset(repo, parent)
    }


    @Test
    fun `initialization works`() {
        val model = TestData.MODELS.random()
        assertNull(vmImpl.modelFilter.value)
        assertNull(vm.modelList.value)
        whenever(repo.getModels(brandId, "")).thenReturn(Response.success(StaticDataSource.factory(listOf(model).map { it.toData() })))

        vmImpl.initialize()

        assertEquals(listOf(model.toData()), vm.modelList.getOrAwait())
        assertEquals("", vmImpl.modelFilter.value)
        verify(repo).getModels(brandId, "")
    }

    @Test
    fun `when there is an error while while initializing show an empty list and pop a message`() {
        assertNull(vmImpl.modelFilter.value)
        assertNull(vm.modelList.value)
        whenever(repo.getModels(brandId, "")).thenReturn(Response.failure(ERROR))

        vmImpl.initialize()

        assertEquals(0, vm.modelList.getOrAwait().count())
        verify(parent).showError(ERROR)
        verify(repo).getModels(brandId, "")
    }

    @Test
    fun `when the filter changes the results change`() {
        assertNull(vmImpl.modelFilter.value)
        assertNull(vm.modelList.value)
        val expected = TestData.MODELS.shuffled().take(30).map { it.toData() }
        whenever(repo.getModels(brandId, FILTER)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))

        vm.newFilter(FILTER, true)

        val values = vm.modelList.getOrAwait().toList()
        assertEquals(FILTER, vmImpl.modelFilter.value)
        verify(repo).getModels(brandId, FILTER)
        verify(parent).hideKeyboard()
        assertEquals(expected.size, values.size)
        assertEquals(expected.size, expected.map { it.id }.intersect(values.map { it.id }).size)
    }

    @Test
    fun `changing the filter that throws an error clears the results `() {
        runBlocking {
            whenever(repo.getModels(brandId, FILTER)).thenReturn(Response.failure(ERROR))

            vm.newFilter(FILTER, false)

            val list = vm.modelList.getOrAwait() //to make sure invokes happen
            verify(repo).getModels(brandId, FILTER)
            verify(parent, never()).hideKeyboard()
            verify(parent).showError(ERROR)

            assertEquals(0, list.size)
        }
    }

    @Test
    fun `pressing add new creates an empty model and shows it`() {

        vm.create()

        val value = vm.model.getOrAwait()
        assertNotNull(value)
        assertEquals(EmptyUUID, value.id)
        assertEquals("", value.name)
    }

    @Test
    fun `pressing save with no new model pressed shows an error`() {

        vm.model.getOrAwait(time = 10, throwError = false) //just to make sure the transformation is observed
        vm.save("hello")

        verify(parent).showError(ArgumentMatchers.anyString())
        verify(parent).showLoading(false)
        verify(app).getString(R.string.error_new_model_not_found)
        verifyNoMoreInteractions(app, repo)
    }

    @Test
    fun `pressing save with an empty named new model shows an error`() {

        vm.create()
        vm.model.getOrAwait()
        vm.save("")

        verify(parent).showError(ArgumentMatchers.anyString())
        verify(parent).showLoading(false)
        verify(app).getString(R.string.error_empty_model_name)
        verifyNoMoreInteractions(app, repo)
    }

    @Test
    fun `pressing save with a new model saves a new item`() {
        runBlocking {
            val name = "hello world"

            whenever(repo.newModel(name, brandId)).thenReturn(Response.success())

            vm.create()
            vm.model.getOrAwait()

            vm.save(name)
            vm.model.getOrAwait(0, throwError = false)

            verify(parent, never()).showError(ArgumentMatchers.anyString())
            verify(parent).showLoading(false)
            assertNull(vm.model.value)
            verify(repo).newModel(name, brandId)
        }
    }

    @Test
    fun `pressing save with a new model which has a problem shows the problem `() {
        runBlocking {
            val name = "hello world"

            whenever(repo.newModel(name, brandId)).thenReturn(Response.failure(ERROR))
            vm.create()
            vm.model.getOrAwait()

            vm.save(name)
            vm.model.getOrAwait()


            verify(parent).showError(APP_STRING)
            verify(parent).showLoading(false)
            verify(app).getString(R.string.error_saving_model)
            assertNotNull(vm.model.value)
            verify(repo).newModel(name, brandId)
        }
    }

    @Test
    fun `pressing edit loads the edited model to memory and displays it for editing`() {
        runBlocking {

            val model = TestData.MODELS.random()
            val id = model.id
            whenever(repo.getModel(id)).thenReturn(Response.success(model.toData()))

            vm.edit(id)

            verify(repo).getModel(id)
            verify(parent).showLoading(false)
            verify(parent, never()).showError(ArgumentMatchers.anyString())
            assertEquals(model.toData(), vm.model.getOrAwait())
        }
    }

    @Test
    fun `pressing edit loads the edited model to memory but there is an error while loading`() {
        runBlocking {
            val model = TestData.MODELS.random()
            val id = model.id
            whenever(repo.getModel(id)).thenReturn(Response.failure(ERROR))

            vm.edit(id)

            verify(repo).getModel(id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertNull(vm.model.getOrAwait(200L, throwError = false))
        }
    }

    @Test
    fun `pressing cancel with a new model clears everything`() {
        vm.create()
        assertNotNull(vm.model.getOrAwait())

        vm.cancel()


        verifyNoMoreInteractions(repo)
        assertNull(vm.model.getOrAwait())
    }

    @Test
    fun `pressing cancel with an edited model clears everything`() {
        runBlocking {
            val model = TestData.MODELS.random()
            val id = model.id
            whenever(repo.getModel(id)).thenReturn(Response.success(model.toData()))
            vm.edit(id)
            assertEquals(model.toData(), vm.model.getOrAwait())
            reset(repo)

            vm.cancel()

            verifyNoMoreInteractions(repo)
            assertNull(vm.model.getOrAwait())
        }
    }

    @Test
    fun `deleting a model already shows an undo message`() {
        runBlocking {
            val deleted = TestData.MODELS.random()
            whenever(repo.deleteModel(deleted.id)).thenReturn(Response.success())

            vm.delete(deleted.id)

            verify(repo).deleteModel(deleted.id)
            verify(parent).showLoading(false)
            verify(parent, never()).showError(ArgumentMatchers.anyString())
            assertTrue(vm.showUndo.getOrAwait())
            assertEquals(deleted.id, vmImpl.lastDeleted.value)
        }
    }

    @Test
    fun `deleting a model with an error`() {
        runBlocking {
            val id = TestData.MODELS.random()
            whenever(repo.deleteModel(id.id)).thenReturn(Response.failure(ERROR))

            vm.delete(id.id)

            verify(repo).deleteModel(id.id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertEquals(null, vm.showUndo.getOrAwait(time = 200L, throwError = false))
            assertNull(vmImpl.lastDeleted.value)
        }
    }

    @Test
    fun `un-deleting a model shows it again`() {
        runBlocking {
            val id = TestData.MODELS.random().id
            vmImpl.lastDeleted.postValue(id)
            vm.showUndo.getOrAwait()
            whenever(repo.restoreModel(id)).thenReturn(Response.success())

            vm.undoLastDelete()

            verify(repo).restoreModel(id)
            verify(parent).showLoading(false)
            verify(parent, never()).showError(ArgumentMatchers.anyString())
            assertNull(vmImpl.lastDeleted.value)
        }
    }

    @Test
    fun `un-deleting a model with an error`() {
        runBlocking {
            val id = TestData.MODELS.random().id
            vmImpl.lastDeleted.postValue(id)
            vm.showUndo.getOrAwait()
            whenever(repo.restoreModel(id)).thenReturn(Response.failure(ERROR))

            vm.undoLastDelete()

            verify(repo).restoreModel(id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertNotNull(vmImpl.lastDeleted.value)
        }
    }

    @Test
    fun `select a model`() {
        runBlocking {
            val model = TestData.MODELS.random()
            val id = model.id
            whenever(repo.getModel(id)).thenReturn(Response.success(model.toData()))

            vm.select(id)

            verify(repo).getModel(id)
            verify(parent).showLoading(false)
            verify(parent, never()).showError(ArgumentMatchers.anyString())
            assertTrue(vm.command.value is ModelCommand.ModelSelected)
            assertEquals(model.toData(), (vm.command.value as ModelCommand.ModelSelected).model)
        }
    }

    @Test
    fun `select a model that cannot be loaded`() {
        runBlocking {
            val brand = TestData.BRANDS.random()
            val id = brand.id
            whenever(repo.getModel(id)).thenReturn(Response.failure(ERROR))

            vm.select(id)

            verify(repo).getModel(id)
            verify(parent).showLoading(false)
            verify(parent).showError(ERROR)
            assertNull(vm.command.getOrAwait(time = 200, throwError = false))
        }
    }

    @Test
    fun `refreshing triggers the load again`() {
        val expected = TestData.MODELS.shuffled().take(30).map { it.toData() }
        whenever(repo.getModels(brandId, FILTER)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))
        vm.newFilter(FILTER, true)
        vm.modelList.getOrAwait() //to trigger the change

        vm.refresh()
        vm.modelList.getOrAwait() //to trigger the change

        verify(repo, times(2)).getModels(brandId, FILTER)
    }
}