package gr.blackswamp.damagereports.vms.reports

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import gr.blackswamp.core.lifecycle.SingleLiveEvent
import gr.blackswamp.core.logging.ILog
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.util.isNullOrBlank
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.repos.IReportRepository
import gr.blackswamp.damagereports.ui.base.ScreenCommand
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.model.ReportHeader
import gr.blackswamp.damagereports.ui.reports.ReportCommand
import gr.blackswamp.damagereports.util.StaticDataSource
import gr.blackswamp.damagereports.vms.ReportData
import gr.blackswamp.damagereports.vms.base.BaseViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportActivityViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportListViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportViewViewModel
import kotlinx.coroutines.launch
import org.koin.core.inject
import java.util.*
import kotlin.contracts.ExperimentalContracts

class ReportViewModel(application: Application, runInit: Boolean = true) : BaseViewModel(application), IReportActivityViewModel, IReportListViewModel, IReportViewViewModel {
    companion object {
        const val TAG = "ReportViewModel"
        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 30
    }

    private val log: ILog by inject()
    private val repo: IReportRepository by inject()
    @VisibleForTesting
    internal val filter = MutableLiveData<String>()
    override val darkTheme: LiveData<Boolean> = repo.darkThemeLive

    init {
        if (runInit) {
            initialize()
        }
    }

    @VisibleForTesting
    internal fun initialize() {
        filter.postValue("")
    }

    //region IReportActivityViewModel implementation
    override val loading = MutableLiveData<Boolean>()
    override val error = SingleLiveEvent<String>()
    // this is used for showing (if needed) the correct fragment and updating the view fragment's data
    override val report = MutableLiveData<Report>()

    override fun toggleTheme() {
        viewModelScope.launch {
            try {
                loading.postValue(true)
                repo.switchTheme()
                loading.postValue(false)
            } catch (t: Throwable) {
                error.setValue(t.message ?: "Error switching theme")
            } finally {
                loading.postValue(false)
            }
        }
    }

    override fun backPressed() {
        if (report.value != null) {
            exitReport()
        } else {
            command.postValue(ScreenCommand.Back)
        }
    }

    //endregion

    //region IReportListViewModel implementation
    @VisibleForTesting
    internal val lastDeleted = MutableLiveData<UUID>()

    override val showUndo: LiveData<Boolean> =
        Transformations.map(lastDeleted) { it != null }

    override var reportHeaderList: LiveData<PagedList<ReportHeader>> =
        Transformations.switchMap(filter, this::dbHeaderToUi)

    override val refreshing = MutableLiveData<Boolean>()

    private fun dbHeaderToUi(filter: String?): LiveData<PagedList<ReportHeader>> {
        log.d(TAG, "transforming for \"$filter\"")
        val response = repo.getReportHeaders(filter ?: "")
        return if (response.hasError) {
            error.postValue(response.errorMessage)
            StaticDataSource.factory(listOf<ReportHeader>())
        } else {
            response.get.map {
                log.d(TAG, "Loaded $it")
                it as ReportHeader
            }
        }.toLiveData(pageSize = LIST_PAGE_SIZE)
    }

    override fun deleteReport(id: UUID) {
        viewModelScope.launch {
            try {
                loading.postValue(true)
                repo.deleteReport(id).throwOrGet()
                lastDeleted.postValue(id)
            } catch (t: Throwable) {
                error.postValue(t.message ?: getString(R.string.error_deleting, id))
                lastDeleted.postValue(null)
            } finally {
                loading.postValue(false)
            }
        }
    }

    override fun dismissedUndo() {
        lastDeleted.postValue(null)
    }

    override fun undoLastDelete() {
        viewModelScope.launch {
            val last = lastDeleted.value
            try {
                if (last == null) {
                    throw Throwable(getString(R.string.error_un_deleting_no_saved_value))
                }
                loading.postValue(true)
                repo.unDeleteReport(last).throwOrGet()
            } catch (t: Throwable) {
                error.postValue(t.message ?: getString(R.string.error_un_deleting, last))
            } finally {
                lastDeleted.postValue(null)
                loading.postValue(false)
            }
        }
    }

    override fun selectReport(id: UUID) = showReport(id, false)

    override fun editReport(id: UUID) = showReport(id, true)

    private fun showReport(id: UUID, inEditMode: Boolean) {
        viewModelScope.launch {
            loading.postValue(true)
            try {
                val data = repo.loadReport(id).throwOrGet()
                editMode.postValue(inEditMode)
                report.postValue(data)
            } catch (t: Throwable) {
                error.postValue(t.message ?: t::class.java.name)
            } finally {
                loading.postValue(false)
            }
        }
    }

    override fun newReport() {
        viewModelScope.launch {
            val newReport = ReportData(EmptyUUID, "", "", null, null, Date(System.currentTimeMillis()), false)
            editMode.postValue(true)
            report.postValue(newReport)
        }
    }

    override fun newReportFilter(filter: String, submitted: Boolean): Boolean {
        this.filter.postValue(filter)
        if (submitted)
            command.postValue(ScreenCommand.HideKeyboard)
        return true
    }

    override fun reloadReports() {
        refreshing.postValue(true)
        this.filter.postValue(null)
        this.filter.postValue(this.filter.value)
        refreshing.postValue(false)
    }

    //endregion

    //region IReportViewViewModel implementation
    @VisibleForTesting
    override val editMode = MutableLiveData<Boolean>()

    override fun pickBrand() {
        if (report.value as? ReportData == null) return
        command.postValue(ReportCommand.ShowBrandSelection)
    }

    override fun pickModel() {
        val current = report.value as? ReportData ?: return
        if (current.brand == null) {
            error.setValue(getString(R.string.error_no_brand_selected))
            return
        }
        command.postValue(ReportCommand.ShowModelSelection(current.brand.id))
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
        command.postValue(ScreenCommand.HideKeyboard)
    }

    override fun editReport() {
        this.editMode.postValue(true)
    }

    override fun exitReport() {
        val report = report.value as ReportData
        if (editMode.value == true && report.changed) {
            command.postValue(ReportCommand.ConfirmDiscard)
        } else if (editMode.value == true && report.id != EmptyUUID) {
            editMode.postValue(false)
        } else {
            exitView()
        }
    }

    @ExperimentalContracts
    override fun confirmDiscardChanges() {
        val reportId = this.report.value?.id
        if (reportId.isNullOrBlank()) {
            exitView()
        } else {
            showReport(reportId, false)
        }
    }

    private fun exitView() {
        editMode.postValue(null)
        report.postValue(null)
    }
    //endregion
}
