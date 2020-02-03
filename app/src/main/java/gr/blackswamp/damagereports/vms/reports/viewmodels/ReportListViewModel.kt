package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.ui.model.ReportHeader
import gr.blackswamp.damagereports.ui.reports.commands.ReportListCommand
import gr.blackswamp.damagereports.vms.base.IBaseViewModel
import java.util.*

interface ReportListViewModel : IBaseViewModel {
    val refreshing: LiveData<Boolean>
    var reportHeaderList: LiveData<PagedList<ReportHeader>>
    val listCommand: LiveData<ReportListCommand>

    fun newReport()
    fun reloadReports()
    fun newReportFilter(filter: String, submitted: Boolean): Boolean
    fun deleteReport(id: UUID)
    fun selectReport(id: UUID)
    fun editReport(id: UUID)
    fun showThemeSettings()
    fun changeTheme(theme: ThemeSetting)
}
