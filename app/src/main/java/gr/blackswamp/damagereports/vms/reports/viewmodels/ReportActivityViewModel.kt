package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.reports.commands.ReportActivityCommand
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

interface ReportActivityViewModel : IBaseViewModel {
    val report: LiveData<Report>
    val showUndo: LiveData<Boolean>
    val loading: LiveData<Boolean>
    val error: LiveData<String>
    val activityCommand: LiveData<ReportActivityCommand>

    fun undoLastDelete()
    fun dismissedUndo()
    fun confirmDiscardChanges()
    fun backPressed()
}
