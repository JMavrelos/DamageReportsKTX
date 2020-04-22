package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.lifecycle.call
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.data.repos.ReportViewRepository
import gr.blackswamp.damagereports.logic.model.ReportData
import gr.blackswamp.damagereports.ui.model.Report
import org.koin.core.inject
import java.util.*

class ReportViewViewModelImpl(application: Application, runInit: Boolean = true) : CoreViewModel(application), ReportViewViewModel {
    companion object {
        const val TAG = "ReportViewViewModel"
    }

    private val repo: ReportViewRepository by inject()
    private val dispatchers: Dispatcher by inject()

    //region live data
    @VisibleForTesting
    internal val filter = MutableLiveData<String>()

    @VisibleForTesting
    internal val lastDeleted = MutableLiveData<UUID>()
    override val report = MutableLiveData<Report>() // this is used for showing (if needed) the correct fragment and updating the view fragment's data

    @VisibleForTesting
    override val editMode = MutableLiveData<Boolean>()
    //endregion

    init {
        if (runInit) {
            initialize()
        }
    }

    @VisibleForTesting
    internal fun initialize() {
        filter.postValue("")
    }

    //region IReportViewViewModel implementation
    override fun pickBrand() {
        if (report.value as? ReportData == null) return
//        activityCommand.postValue(ReportActivityCommand.ShowBrandSelection)

    }

    override fun pickModel() {
        val current = report.value as? ReportData ?: return
//        activityCommand.postValue(ReportActivityCommand.ShowModelSelection(UUID.randomUUID()))
        if (current.brand == null) {
//            error.setValue(getString(R.string.error_no_brand_selected))
            return
        }
//        activityCommand.postValue(ReportActivityCommand.ShowModelSelection(current.brand.id))
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
