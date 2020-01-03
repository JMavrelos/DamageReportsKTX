package gr.blackswamp.damagereports.vms.reports.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.ui.model.Report

interface IReportViewViewModel {
    val report: LiveData<Report>

    fun pickModel()
    fun pickBrand()
    fun saveReport()


}