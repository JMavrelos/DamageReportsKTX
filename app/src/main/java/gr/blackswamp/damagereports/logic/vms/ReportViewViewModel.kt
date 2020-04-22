package gr.blackswamp.damagereports.logic.vms

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.ui.model.Report

interface ReportViewViewModel {
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