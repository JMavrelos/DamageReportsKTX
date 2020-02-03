package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.reports.ReportCommand
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

interface ReportActivityViewModel : IBaseViewModel {
    val report: LiveData<Report>
    val showUndo: LiveData<Boolean>
    val loading: LiveData<Boolean>
    val error: LiveData<String>
    val activityCommand: LiveData<ReportCommand>

    fun undoLastDelete()
    fun dismissedUndo()
    fun confirmDiscardChanges()
    fun backPressed()
    fun changeTheme(themeSetting: ThemeSetting)
}
