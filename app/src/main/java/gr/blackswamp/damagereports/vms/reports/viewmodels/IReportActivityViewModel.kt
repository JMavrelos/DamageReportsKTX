package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData

interface IReportActivityViewModel {
    val error: LiveData<String>
}
