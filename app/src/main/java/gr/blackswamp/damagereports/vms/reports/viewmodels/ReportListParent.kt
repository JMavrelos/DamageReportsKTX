package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.ui.model.ReportHeader
import java.util.*

interface ReportListParent {
    val themeSelection: LiveData<ThemeSetting>
    val refreshing: LiveData<Boolean>
    var reportHeaderList: LiveData<PagedList<ReportHeader>>

    fun newReport()
    fun reloadReports()
    fun newReportFilter(filter: String, submitted: Boolean): Boolean
    fun deleteReport(id: UUID)
    fun selectReport(id: UUID)
    fun editReport(id: UUID)
    fun showThemeSettings()
    fun changeTheme(theme: ThemeSetting)
    fun closeThemeSelection()
}
