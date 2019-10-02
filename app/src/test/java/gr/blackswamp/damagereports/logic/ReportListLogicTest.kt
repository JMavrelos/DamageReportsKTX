package gr.blackswamp.damagereports.logic
//
//import androidx.paging.PagedList
//import gr.blackswamp.core.TestDataSource
//import gr.blackswamp.core.argumentCaptor
//import gr.blackswamp.core.schedulers.TestDispatchers
//import gr.blackswamp.core.util.TestLog
//import gr.blackswamp.damagereports.UnitTestData
//import gr.blackswamp.damagereports.data.db.IDatabase
//import gr.blackswamp.damagereports.ui.model.ReportHeader
//import gr.blackswamp.damagereports.viewmodel.IReportViewModel
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito.*
//import org.mockito.Mockito.`when` as whenever
//
//class ReportListLogicTest {
//    private lateinit var logic: ReportListLogic
//    private lateinit var vm: IReportViewModel
//    private lateinit var db: IDatabase
//
//    @Before
//    fun setUp() {
//        vm = mock(IReportViewModel::class.java)
//        db = mock(IDatabase::class.java)
//        logic = ReportListLogic(vm, db, TestDispatchers, TestLog)
//    }
//
//    @After
//    fun tearDown() {
//
//    }
//
//    @Test
//    fun startingWithNoData() {
//        whenever(db.loadReportHeaders("")).thenReturn(TestDataSource.TestDataSourceFactory(listOf()))
//        val captor = argumentCaptor<PagedList<ReportHeader>>()
//
//        logic.initialize()
//
//        verify(vm).showReports(captor.capture())
//        assertEquals(0, captor.firstValue.size)
//        assertEquals("", logic.mFilter.value)
//    }
//
//    @Test
//    fun startingWithData() {
//        val captor = argumentCaptor<PagedList<ReportHeader>>()
//        whenever(db.loadReportHeaders("")).thenReturn(TestDataSource.TestDataSourceFactory(UnitTestData.REPORT_HEADERS.sortedByDescending { it.date }))
//
//        logic.initialize()
//
//        verify(db).loadReportHeaders("")
//        verify(vm).showReports(captor.capture())
//        val values = captor.firstValue
//        assertEquals(ReportListLogic.PAGE_SIZE, values.size)
//        assertEquals(ReportListLogic.PAGE_SIZE, UnitTestData.REPORT_HEADERS.sortedByDescending { it.date }.subList(0, ReportListLogic.PAGE_SIZE).intersect(values).size)
//        assertEquals("", logic.mFilter.value)
//    }
//
//    @Test
//    fun filterReports() {
//        val filter = "12"
//        val expected = UnitTestData.REPORT_HEADERS.filter { it.name.contains(filter) || it.description.contains(filter) }.sortedByDescending { it.date }
//        val captor = argumentCaptor<PagedList<ReportHeader>>()
//        println("Expected ${expected.size}")
//        whenever(db.loadReportHeaders("")).thenReturn(TestDataSource.TestDataSourceFactory(UnitTestData.REPORT_HEADERS))
//        whenever(db.loadReportHeaders(filter)).thenReturn(TestDataSource.TestDataSourceFactory(expected))
//
//        logic.initialize()
//        logic.setListFilter(filter)
//
//        verify(db).loadReportHeaders(filter)
//        verify(vm, times(2)).showReports(captor.capture())
//        assertEquals(20, captor.firstValue.size)
//        assertEquals(Math.min(expected.size, 20), captor.secondValue.size)
//        assertEquals(Math.min(expected.size, 20), expected.intersect(captor.secondValue).size)
//        assertEquals(filter, logic.mFilter.value)
//    }
//
//    @Test
//    fun clearReportFilter() {
////        logic.initialize()
////        logic.mFilter.onNext("12")
////
////        whenever(repo.searchReportHeaders("")).thenReturn(Observable.just(UnitTestData.REPORT_HEADERS))
////
////        logic.setListFilter("")
////        verify(repo).searchReportHeaders("")
////        verify(vm).showReports(UnitTestData.REPORT_HEADERS)
////        assertEquals("", logic.mFilter.value)
//
//    }
//
//    @Test
//    fun refreshReports() {
////        val filter = "12"
////        whenever(repo.searchReportHeaders("")).thenReturn(Observable.just(UnitTestData.REPORT_HEADERS))
////        logic.initialize()
////        logic.mFilter.onNext("12")
////        val expected = UnitTestData.REPORT_HEADERS.filter { it.name.contains(filter) || it.description.contains(filter) }
////        whenever(repo.searchReportHeaders("")).thenReturn(Observable.just(expected))
////
////        logic.refresh()
////
////        verify(repo).searchReportHeaders(filter)
////        verify(vm).showReports(expected)
////        assertEquals(filter, logic.mFilter.value)
//    }
//
//    @Test
//    fun userSelectedReport() {
//
//    }
//
//    @Test
//    fun userSelectedReportThatCannotBeFound() {
//
//    }
//
//    @Test
//    fun userDeletesReport() {
//
//    }
//
//    @Test
//    fun userDeletesReportThatCannotBeFound() {
//
//    }
//
//    @Test
//    fun createANewReport() {
//
//    }
//
//
//}