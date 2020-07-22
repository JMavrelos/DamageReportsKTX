package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.logic.commands.ReportViewCommand
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.model.ReportDamage

interface ReportViewViewModel {
    val report: LiveData<Report>
    val damages: LiveData<PagedList<ReportDamage>>
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