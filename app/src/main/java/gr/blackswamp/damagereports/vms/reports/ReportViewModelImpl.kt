package gr.blackswamp.damagereports.vms.reports

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.lifecycle.call
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.util.isNullOrBlank
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.repos.ReportRepository
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.model.ReportHeader
import gr.blackswamp.damagereports.ui.reports.commands.ReportActivityCommand
import gr.blackswamp.damagereports.vms.ReportData
import gr.blackswamp.damagereports.vms.base.BaseViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.ReportListParent
import gr.blackswamp.damagereports.vms.reports.viewmodels.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.ReportViewParent
import kotlinx.coroutines.launch
import org.koin.core.inject
import timber.log.Timber
import java.util.*
import kotlin.contracts.ExperimentalContracts

class ReportViewModelImpl(application: Application, runInit: Boolean = true) : BaseViewModel(application), ReportViewModel, ReportListParent, ReportViewParent {
    companion object {
        const val TAG = "ReportViewModel"
        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 30
    }

    private val repo: ReportRepository by inject()
    private val dispatchers: IDispatchers by inject()
    //region live data
    @VisibleForTesting
    internal val filter = MutableLiveData<String>()
    override val themeSetting: LiveData<ThemeSetting> = repo.themeSettingLive
    override val themeSelection = MutableLiveData<ThemeSetting>(null)
    override val loading = MutableLiveData<Boolean>()
    @VisibleForTesting
    internal val lastDeleted = MutableLiveData<UUID>()
    override val refreshing = MutableLiveData<Boolean>()
    override val showUndo: LiveData<Boolean> = Transformations.map(lastDeleted) { it != null }
    override val error = LiveEvent<String>()
    override val activityCommand = LiveEvent<ReportActivityCommand>()
    override val report = MutableLiveData<Report>() // this is used for showing (if needed) the correct fragment and updating the view fragment's data
    override var reportHeaderList: LiveData<PagedList<ReportHeader>> = Transformations.switchMap(filter, this::dbHeaderToUi)
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

    //region IReportActivityViewModel implementation
    override fun showThemeSettings() {
        themeSelection.postValue(repo.themeSetting)
    }

    override fun backPressed() {
        if (report.value != null) {
            exitReport()
        } else {
            back.call()
        }
    }
    //endregion

    //region IReportListViewModel implementation
    private fun dbHeaderToUi(filter: String?): LiveData<PagedList<ReportHeader>> {
        Timber.d("transforming for \"$filter\"")
        val response = repo.getReportHeaders(filter ?: "")
        return if (response.hasError) {
            error.postValue(response.errorMessage)
            StaticDataSource.factory(listOf<ReportHeader>())
        } else {
            response.get.map {
                Timber.d("Loaded $it")
                it as ReportHeader
            }
        }.toLiveData(pageSize = LIST_PAGE_SIZE)
    }

    override fun deleteReport(id: UUID) {
        viewModelScope.launch(dispatchers.UI) {
            try {
                loading.postValue(true)
                repo.deleteReport(id).getOrThrow
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
        viewModelScope.launch(dispatchers.UI) {
            val last = lastDeleted.value
            try {
                if (last == null) {
                    throw Throwable(getString(R.string.error_un_deleting_no_saved_value))
                }
                loading.postValue(true)
                repo.unDeleteReport(last).getOrThrow
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
        viewModelScope.launch(dispatchers.UI) {
            loading.postValue(true)
            try {
                val data = repo.loadReport(id).getOrThrow
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
        val newReport = ReportData(EmptyUUID, "", "", null, null, Date(System.currentTimeMillis()), false)
        editMode.value = true
        report.value = newReport
    }

    override fun newReportFilter(filter: String, submitted: Boolean): Boolean {
        this.filter.postValue(filter)
        if (submitted)
            hideKeyboard.call()
        return true
    }

    override fun reloadReports() {
        refreshing.postValue(true)
        this.filter.postValue(null)
        this.filter.postValue(this.filter.value)
        refreshing.postValue(false)
    }

    override fun changeTheme(theme: ThemeSetting) {
        repo.setTheme(theme)
        themeSelection.postValue(null)
    }

    override fun closeThemeSelection() {
        themeSelection.postValue(null)
    }

    //endregion

    //region IReportViewViewModel implementation
    override fun pickBrand() {
        if (report.value as? ReportData == null) return
        activityCommand.postValue(ReportActivityCommand.ShowBrandSelection)

    }

    override fun pickModel() {
        val current = report.value as? ReportData ?: return
        activityCommand.postValue(ReportActivityCommand.ShowModelSelection(UUID.randomUUID()))
        if (current.brand == null) {
            error.setValue(getString(R.string.error_no_brand_selected))
            return
        }
        activityCommand.postValue(ReportActivityCommand.ShowModelSelection(current.brand.id))
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
            activityCommand.postValue(ReportActivityCommand.ConfirmDiscard)
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
