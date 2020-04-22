package gr.blackswamp.damagereports.logic.reports

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.data.Response
import gr.blackswamp.core.testing.AndroidKoinTest
import gr.blackswamp.core.testing.MainCoroutineScopeRule
import gr.blackswamp.core.testing.TestDispatcher
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.repos.ReportListRepository
import gr.blackswamp.damagereports.logic.model.BrandData
import gr.blackswamp.damagereports.logic.model.ModelData
import gr.blackswamp.damagereports.logic.model.ReportData
import gr.blackswamp.damagereports.logic.vms.ReportViewViewModelImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import java.util.*
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
class ReportViewViewModelTest : AndroidKoinTest() {
    companion object {
        private const val FILTER = "a filter"
        private const val ERROR = " there was an error"
    }

    private val repo: ReportListRepository = mock(ReportListRepository::class.java)
    override val modules: Module = module {
        single<Dispatcher> { TestDispatcher }
        single { repo }
    }

    private lateinit var vm: ReportViewViewModelImpl

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    override fun setUp() {
        super.setUp()
        vm = ReportViewViewModelImpl(app, false)
        reset(repo)
    }

    @Test
    fun `new report shows correctly`() {
        runBlockingTest {
//    //            vm.newReport()

            val report = vm.report.value!!
            val timeDiff = System.currentTimeMillis() - report.created.time

            assertTrue(vm.editMode.value!!)
            assertTrue(timeDiff in 0..100) //this may fail if the test takes too much but 100 ms is long enough
            assertEquals(EmptyUUID, report.id)
            assertNull(report.brandName)
            assertNull(report.modelName)
            assertEquals("", report.description)
            assertEquals("", report.name)
            assertFalse((report as ReportData).changed)
        }
    }


    @Test
    fun `when name changes then the selected report is updated`() {
//            vm.newReport()

        vm.nameChanged("hello world")

        assertTrue((vm.report.value as ReportData).changed)
        assertEquals("hello world", vm.report.value!!.name)
    }

    @Test
    fun `when description changes then the selected report is updated`() {

//            vm.newReport()

        vm.descriptionChanged("hello world")

        assertTrue((vm.report.value as ReportData).changed)
        assertEquals("hello world", vm.report.value!!.description)
    }


    @Test
    fun `user presses exit after opening a new report with no changes made`() {
//            vm.newReport()

        vm.exitReport()

        assertNull(vm.report.value)
        assertNull(vm.editMode.value)
    }

    @Test
    fun `user presses exit after opening a new report with changes made`() {
//            vm.newReport()
        vm.nameChanged("wlke;lq")

        vm.exitReport()

//        assertTrue(vm.activityCommand.value is ReportActivityCommand.ConfirmDiscard)
        assertNotNull(vm.report.value)
    }

    @Test
    fun `the user presses exit on a non edited screen`() {
        val report =
            ReportData(
                UUID.randomUUID()
                , "a name"
                , "a description"
                , BrandData(UUID.randomUUID(), "a brand")
                , ModelData(UUID.randomUUID(), " a model", UUID.randomUUID())
                , Date(0)
            )
        vm.report.value = report
        vm.editMode.value = false

        vm.exitReport()

        assertNull(vm.report.value)
    }

    @Test
    fun `the user presses exit we are in edit mode but no changes have been made`() {
        val report = ReportData(
            UUID.randomUUID()
            , "a name"
            , "a description"
            , BrandData(UUID.randomUUID(), "a brand")
            , ModelData(UUID.randomUUID(), " a model", UUID.randomUUID())
            , Date(0),
            false
        )

        vm.report.value = report
        vm.editMode.value = true

        vm.exitReport()

        assertFalse(vm.editMode.value!!)
        assertEquals(report, vm.report.value)
    }

    @Test
    fun `user presses exit in edit mode and there are changes`() {
        val id = UUID.randomUUID()
        val report = ReportData(
            id, "a name"
            , "a description"
            , BrandData(UUID.randomUUID(), "a brand")
            , ModelData(UUID.randomUUID(), " a model", UUID.randomUUID())
            , Date(0)
            , true
        )
        vm.report.value = report
        vm.editMode.value = true

        vm.exitReport()

//        assertTrue(vm.activityCommand.value is ReportActivityCommand.ConfirmDiscard)
    }


    @ExperimentalContracts
    @Test
    fun `when the user confirms discard on a new report back is pressed`() {
        vm.report.value = ReportData(EmptyUUID, "", "", null, null, Date(), true)
        vm.editMode.value = true

//        vm.confirmDiscardChanges()

        assertNull(vm.report.value)
    }

    @ExperimentalContracts
    @Test
    fun `when the user confirms discard on an edited report it is reloaded`() {
        runBlockingTest {
            val id = UUID.randomUUID()
            val report = ReportData(
                id
                , "a name"
                , "a description"
                , BrandData(UUID.randomUUID(), "a brand")
                , ModelData(UUID.randomUUID(), " a model", UUID.randomUUID())
                , Date(0)
            )

            vm.report.value = report.copy(changed = true)
            vm.editMode.value = true
            vm.nameChanged("hello")
            whenever(repo.loadReport(id)).thenReturn(Response.success(report))

//            vm.confirmDiscardChanges()

            assertFalse(vm.editMode.value!!)
            assertEquals(report, vm.report.value)
        }
    }

    @Test
    fun `when back is pressed in list it is sent to the system to be evaluated`() {
        vm.report.value = null
//        vm.backPressed()

        assertNotNull(vm.back.value)
    }

    @Test
    fun `when back is pressed on an edited report then the discard dialog shows`() {
        val id = UUID.randomUUID()
        val report = ReportData(
            id, "a name"
            , "a description"
            , BrandData(UUID.randomUUID(), "a brand")
            , ModelData(UUID.randomUUID(), " a model", UUID.randomUUID())
            , Date(0)
            , true
        )
        vm.report.value = report
        vm.editMode.value = true

//        vm.backPressed()

//        assertTrue(vm.activityCommand.value is ReportActivityCommand.ConfirmDiscard)
    }

    @Test
    fun `when the user tries to pick a model before trying to pick a brand then a message is shown`() {
        val id = UUID.randomUUID()
        vm.report.value = ReportData(id, "name", "descr", null, null, Date(0), true)

        vm.editMode.value = true
        vm.pickModel()

//        assertEquals(APP_STRING, vm.error.value)
        verify(app).getString(R.string.error_no_brand_selected)
    }

    @Test
    fun `when the user tries to pick a model with no current selection nothing happens`() {
        vm.report.value = null

        vm.pickModel()

//        assertNull(vm.activityCommand.value)
    }

    @Test
    fun `when the user tries to pick a brand with no current selection nothing happens`() {
        vm.report.value = null

        vm.pickBrand()

//        assertNull(vm.activityCommand.value)
    }

    @Test
    fun `when the user tries to pick a brand with a selection a signal is sent to show the brand screen`() {
        val id = UUID.randomUUID()
        vm.report.value = ReportData(id, "name", "descr", null, null, Date(0), true)

        vm.pickBrand()

//        assertEquals(ReportActivityCommand.ShowBrandSelection, vm.activityCommand.value)
    }

    @Test
    fun `when the user tries to pick a model with a valid selection a signal is sent to show the brand screen`() {
        val id = UUID.randomUUID()
        val brandId = UUID.randomUUID()
        vm.report.value = ReportData(
            id,
            "name",
            "descr",
            BrandData(brandId, "brand name"),
            null,
            Date(0),
            true
        )

        vm.pickModel()

//        assertTrue(vm.activityCommand.value is ReportActivityCommand.ShowModelSelection)

//        assertEquals(brandId, (vm.activityCommand.value as ReportActivityCommand.ShowModelSelection).brandId)
    }
}