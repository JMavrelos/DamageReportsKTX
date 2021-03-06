package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.logic.commands.ReportListCommand
import gr.blackswamp.damagereports.ui.model.ReportHeader
import java.util.*

interface ReportListViewModel {
    val themeSelection: LiveData<ThemeSetting>
    val refreshing: LiveData<Boolean>
    val reportHeaderList: LiveData<PagedList<ReportHeader>>
    val showUndo: LiveData<Boolean>
    val command: LiveData<ReportListCommand>

    fun newReport()
    fun reloadReports()
    fun newReportFilter(filter: String, submitted: Boolean): Boolean
    fun deleteReport(id: UUID)
    fun selectReport(id: UUID)
    fun editReport(id: UUID)
    fun showThemeSettings()
    fun changeTheme(theme: ThemeSetting)
    fun closeThemeSelection()
    fun undoLastDelete()
    fun dismissedUndo()
}
