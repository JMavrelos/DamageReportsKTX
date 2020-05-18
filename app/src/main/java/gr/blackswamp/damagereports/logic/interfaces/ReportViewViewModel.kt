package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.logic.commands.ReportViewCommand
import gr.blackswamp.damagereports.ui.model.Report

interface ReportViewViewModel {
    val report: LiveData<Report>
    val editMode: LiveData<Boolean>
    val command: LiveData<ReportViewCommand>

    fun pickModel()
    fun pickBrand()
    fun saveReport()
    fun editReport()
    fun exitReport()
    fun confirmDiscardChanges()
    fun nameChanged(name: String)
    fun descriptionChanged(description: String)
//    val viewNavIcon: MutableLiveData<Int>
}