package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

interface IReportActivityViewModel : IBaseViewModel {
    val showUndo: LiveData<Boolean>
    val loading: LiveData<Boolean>
    val error: LiveData<String>

    fun undoLastDelete()
    fun dismissedUndo()
}
