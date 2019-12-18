package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.ui.reports.commands.ReportListCommand
import gr.blackswamp.damagereports.vms.base.IBaseViewModel
import java.util.*

interface IReportListViewModel : IBaseViewModel {
    val reportListCommands: LiveData<ReportListCommand>

    fun newReport()
    fun reloadReports()
    fun loadNextReports(current: Int)
    fun newReportFilter(filter: String, submitted: Boolean): Boolean
    fun deleteReport(id: UUID)
    fun selectReport(id: UUID)
    fun toggleTheme()
}
