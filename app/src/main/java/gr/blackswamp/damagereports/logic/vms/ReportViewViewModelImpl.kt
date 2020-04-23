package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.lifecycle.call
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.util.toThrowable
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.repos.ReportViewRepository
import gr.blackswamp.damagereports.logic.commands.ReportViewCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ReportViewViewModel
import gr.blackswamp.damagereports.logic.model.ReportData
import gr.blackswamp.damagereports.ui.model.Report
import org.koin.core.inject

class ReportViewViewModelImpl(application: Application, private val parent: FragmentParent, report: Report, inEditMode: Boolean, runInit: Boolean = true) :
    CoreViewModel(application),
    ReportViewViewModel {
    companion object {
        const val TAG = "ReportViewViewModel"
    }

    private val repo: ReportViewRepository by inject()

    //region live data
    override val report = MutableLiveData<Report>() // this is used for showing the changes to the current report
    override val command = LiveEvent<ReportViewCommand>()
    private val reportData get() = report.value as? ReportData

    @VisibleForTesting
    override val editMode = MutableLiveData<Boolean>()
    //endregion

    init {
        if (runInit) {
            initialize(report, inEditMode)
        }
    }

    @VisibleForTesting
    internal fun initialize(report: Report, inEditMode: Boolean) {
        try {
            if (report !is ReportData)
                throw getString(R.string.error_invalid_report_data).toThrowable()
            editMode.postValue(inEditMode)
            this.report.postValue(report)
        } catch (t: Throwable) {
            parent.showError(t.message!!)
        }
    }

    //region IReportViewViewModel implementation
    override fun pickBrand() {
        if (reportData == null) return
        command.postValue(ReportViewCommand.ShowBrandSelect)
    }

    override fun pickModel() {
        val current = reportData ?: return
        if (current.brand == null) {
            parent.showError(getString(R.string.error_no_brand_selected))
            return
        }
        command.postValue(ReportViewCommand.ShowModelSelect(current.brand))
    }

    override fun nameChanged(name: String) {
        val current = report.value as? ReportData ?: return
        report.postValue(current.copy(name = name, changed = true))
    }

    override fun descriptionChanged(description: String) {
        val current = report.value as? ReportData ?: return
        report.postValue(current.copy(description = description, changed = true))
    }

    override fun saveReport() {
        this.editMode.postValue(false)
        hideKeyboard.call()
    }

    override fun editReport() {
        this.editMode.postValue(true)
    }

    override fun exitReport() {
        val report = report.value as ReportData
        if (editMode.value == true && report.changed) {
//            activityCommand.postValue(ReportActivityCommand.ConfirmDiscard)
        } else if (editMode.value == true && report.id != EmptyUUID) {
            editMode.postValue(false)
        } else {
            exitView()
        }
    }

    private fun exitView() {
        editMode.postValue(null)
        report.postValue(null)
    }
    //endregion
}
