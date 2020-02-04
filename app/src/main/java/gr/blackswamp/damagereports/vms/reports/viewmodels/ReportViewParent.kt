package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.ui.model.Report

interface ReportViewParent {
    val report: LiveData<Report>
    val editMode: LiveData<Boolean>
    fun pickModel()
    fun pickBrand()
    fun saveReport()
    fun editReport()
    fun exitReport()
    fun nameChanged(name: String)
    fun descriptionChanged(description: String)
//    val viewNavIcon: MutableLiveData<Int>
}