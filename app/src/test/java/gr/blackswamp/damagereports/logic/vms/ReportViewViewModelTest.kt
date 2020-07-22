package gr.blackswamp.damagereports.logic.vms

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
import gr.blackswamp.damagereports.data.repos.ReportViewRepository
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.logic.commands.ReportViewCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.model.BrandData
import gr.blackswamp.damagereports.logic.model.ReportDamageData
import gr.blackswamp.damagereports.logic.model.ReportData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.random.Random

@ExperimentalCoroutinesApi
class ReportViewViewModelTest : KoinUnitTest() {
    companion object {
        private const val ERROR = " there was an error"
    }

    private val repo: ReportViewRepository = mock(ReportViewRepository::class.java)
    override val modules: Module = module {
        single<Dispatcher> { TestDispatcher }
        single { repo }
    }

    private lateinit var vm: ReportViewViewModelImpl
    private val parent = mock(FragmentParent::class.java)
    private val newReport = ReportData(
        EmptyUUID
        , model = null
        , brand = null
        , created = Date()
        , changed = false
    )
    lateinit var report: ReportData

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    override fun setUp() {
        super.setUp()
        val reportEntity = TestData.REPORTS.random()
        val brand = TestData.BRANDS.first { it.id == reportEntity.brand }
        val model = TestData.MODELS.first { it.id == reportEntity.model }
        report = reportEntity.toData(brand, model)

        //initial values do not matter, we call initialize manually
        vm = ReportViewViewModelImpl(app, parent, report, inEditMode = false, runInit = false)

        reset(repo)
    }

    @Test
    fun `initialize for view`() {
        runBlockingTest {
            vm.initialize(report, false)

            val shown = vm.report.value!!

            assertFalse(vm.editMode.value!!)
            assertEquals(report.id, shown.id)
            assertEquals(report.brandName, shown.brandName)
            assertEquals(report.modelName, shown.modelName)
            assertEquals(report.description, shown.description)
            assertEquals(report.name, shown.name)
            assertFalse(report.changed)
        }
    }

    @Test
    fun `initialize on new`() {
        runBlockingTest {
            vm.initialize(newReport, true)

            val shown = vm.report.value!!

            assertTrue(vm.editMode.value!!)
            assertEquals(newReport.id, shown.id)
            assertEquals(newReport.brandName, shown.brandName)
            assertEquals(newReport.modelName, shown.modelName)
            assertEquals(newReport.description, shown.description)
            assertEquals(newReport.name, shown.name)
            assertFalse(newReport.changed)
        }
    }

    @Test
    fun `when name changes then the selected report is updated`() {
        vm.initialize(report, true)

        vm.nameChanged("hello world")

        assertTrue((vm.report.value as ReportData).changed)
        assertEquals("hello world", vm.report.value!!.name)
    }

    @Test
    fun `when description changes then the selected report is updated`() {
        vm.initialize(report, true)

        vm.descriptionChanged("hello world")

        assertTrue((vm.report.value as ReportData).changed)
        assertEquals("hello world", vm.report.value!!.description)
    }

    @Test
    fun `user presses exit after opening a new report with no changes made`() {
        vm.initialize(newReport, true)

        vm.exitReport()

        assertEquals(ReportViewCommand.MoveBack, vm.command.value)
    }

    @Test
    fun `user presses exit after opening a new report with changes made`() {
        vm.initialize(newReport.copy(changed = true), true)

        vm.exitReport()

        assertTrue(vm.command.value is ReportViewCommand.ConfirmDiscard)
        assertNotNull(vm.report.value)
    }

    @Test
    fun `the user presses exit while viewing an existing report`() {
        vm.initialize(report, false)

        vm.exitReport()

        assertTrue(vm.command.value is ReportViewCommand.MoveBack)
    }

    @Test
    fun `the user presses exit while editing an existing report but no changes have been made`() {
        vm.initialize(report, true)

        vm.exitReport()

        assertFalse(vm.editMode.value!!)
        assertEquals(report, vm.report.value)
    }

    @Test
    fun `user presses exit while viewing an existing report and there are changes`() {
        vm.initialize(report.copy(changed = true), true)

        vm.exitReport()

        assertTrue(vm.command.value is ReportViewCommand.ConfirmDiscard)
    }

    @ExperimentalContracts
    @Test
    fun `when the user confirms discard on a new report back is pressed`() {
        vm.report.value = ReportData(EmptyUUID, "", "", null, null, Date(), true)
        vm.editMode.value = true

        vm.confirmDiscardChanges()

        assertTrue(vm.command.value is ReportViewCommand.MoveBack)
    }

    @ExperimentalContracts
    @Test
    fun `when the user confirms discard on an edited report it is reloaded`() {
        runBlocking {
            vm.initialize(report.copy(name = "this is changed", changed = true), true)
            whenever(repo.loadReport(report.id)).thenReturn(Response.success(report))

            vm.confirmDiscardChanges()

            assertEquals(report, vm.report.value)
            assertFalse(vm.editMode.value!!)
            verify(parent, never()).showError(anyString())
            verify(parent).showLoading(false)
        }
    }

    @ExperimentalContracts
    @Test
    fun `when the user confirms discard on an edited report it is reloaded but the reload fails`() {
        runBlocking {
            val changed = report.copy(name = "this is changed", changed = true)
            vm.initialize(changed, true)
            whenever(repo.loadReport(report.id)).thenReturn(Response.failure(ERROR))

            vm.confirmDiscardChanges()

            assertEquals(changed, vm.report.value)
            verify(parent).showError(ERROR)
            verify(parent).showLoading(false)
            assertTrue(vm.editMode.value!!)
        }
    }

    @Test
    fun `when the user confirms discard on a new report we exit`() {
        vm.initialize(newReport.copy(changed = true), true)

        vm.confirmDiscardChanges()

        assertEquals(ReportViewCommand.MoveBack, vm.command.value)
    }

    @Test
    fun `when the user tries to pick a model before trying to pick a brand then a message is shown`() {
        vm.initialize(newReport, true)

        vm.pickModel()

        verify(app).getString(R.string.error_no_brand_selected)
        verify(parent).showError(APP_STRING)
    }

    @Test
    fun `when the user tries to pick a brand with a selection a signal is sent to show the brand screen`() {
        vm.initialize(newReport, true)

        vm.pickBrand()

        assertEquals(ReportViewCommand.ShowBrandSelect, vm.command.value)
    }

    @Test
    fun `when the user tries to pick a model with a valid selection a signal is sent to show the brand screen`() {
        val brand = BrandData(UUID.randomUUID(), "brandName")
        vm.initialize(newReport.copy(brand = brand, changed = true), true)

        vm.pickModel()


        assertTrue(vm.command.value is ReportViewCommand.ShowModelSelect)

        assertEquals(brand, (vm.command.value as ReportViewCommand.ShowModelSelect).brand)
    }

    @Test
    fun `when the view initializes damages are loaded`() {
        val expected = TestData.DAMAGES.filter { it.report == report.id }.map { ReportDamageData(it.id, it.name, Random.nextInt(), Random.nextInt(), Random.nextDouble().toBigDecimal()) }
        whenever(repo.getDamageHeadersList(report.id)).thenReturn(Response.success(StaticDataSource.factory(expected, false)))

        vm.initialize(report, true)

        val damages = vm.damages.getOrAwait().toList()

        assertEquals(expected.size, damages.size)
        assertEquals(expected.size, expected.map { it.id }.intersect(damages.map { it.id }).size)
    }

    @Test
    fun `when the view initializes with an error in damages a message is shown`() {
        whenever(repo.getDamageHeadersList(report.id)).thenReturn(Response.failure(ERROR))

        vm.initialize(report, true)

        val damages = vm.damages.getOrAwait(throwError = false)

        assertEquals(0, damages.size)
        verify(parent).showError(ERROR)
    }
}