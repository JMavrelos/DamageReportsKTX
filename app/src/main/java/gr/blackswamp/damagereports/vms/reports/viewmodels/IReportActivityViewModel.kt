package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.core.lifecycle.SingleLiveEvent
import gr.blackswamp.damagereports.ui.reports.commands.ReportActivityCommand
import gr.blackswamp.damagereports.ui.reports.commands.ReportListCommand
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

interface IReportActivityViewModel : IBaseViewModel {
    val loading: LiveData<Boolean>
    val error: LiveData<String>
    val reportActivityCommands: LiveData<ReportActivityCommand>
}
