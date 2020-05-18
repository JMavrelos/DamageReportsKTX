package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.lifecycle.call
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.repos.ReportListRepository
import gr.blackswamp.damagereports.logic.commands.ReportListCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ReportListViewModel
import gr.blackswamp.damagereports.logic.model.ReportData
import gr.blackswamp.damagereports.ui.model.ReportHeader
import kotlinx.coroutines.launch
import org.koin.core.inject
import timber.log.Timber
import java.util.*

class ReportListViewModelImpl(application: Application, val parent: FragmentParent, runInit: Boolean = true) : CoreViewModel(application),
    ReportListViewModel {
    companion object {
        const val TAG = "ReportViewModel"

        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 30
    }

    private val repo: ReportListRepository by inject()

    //region live data
    @VisibleForTesting
    internal val filter = MutableLiveData<String>()
    override val themeSelection = MutableLiveData<ThemeSetting>(null)
    @VisibleForTesting
    internal val lastDeleted = MutableLiveData<UUID>()
    override val command = LiveEvent<ReportListCommand>()
    override val showUndo: LiveData<Boolean> = Transformations.map(lastDeleted) { it != null }
    override val refreshing = MutableLiveData<Boolean>()
    override var reportHeaderList: LiveData<PagedList<ReportHeader>> = filter.switchMap(this::dbHeaderToUi)
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
    //endregion

    //region IReportListViewModel implementation
    private fun dbHeaderToUi(filter: String?): LiveData<PagedList<ReportHeader>> {
        Timber.d("transforming for \"$filter\"")
        val response = repo.getReportHeaders(filter ?: "")
        return if (response.hasError) {
            parent.showError(response.errorMessage)
            StaticDataSource.factory(listOf<ReportHeader>())
        } else {
            response.get.map {
                Timber.d("Loaded $it")
                it as ReportHeader
            }
        }.toLiveData(pageSize = LIST_PAGE_SIZE)
    }

    override fun deleteReport(id: UUID) {
        launch {
            try {
                parent.showLoading(true)
                repo.deleteReport(id).getOrThrow
                lastDeleted.postValue(id)
            } catch (t: Throwable) {
                parent.showError(t.message ?: getString(R.string.error_deleting, id))
                lastDeleted.postValue(null)
            } finally {
                parent.showLoading(false)
            }
        }
    }

    override fun dismissedUndo() {
        lastDeleted.postValue(null)
    }

    override fun undoLastDelete() {
        launch {
            val last = lastDeleted.value
            try {
                if (last == null) {
                    throw Throwable(getString(R.string.error_un_deleting_no_saved_value))
                }
                parent.showLoading(true)
                repo.unDeleteReport(last).getOrThrow
            } catch (t: Throwable) {
                parent.showError(t.message ?: getString(R.string.error_un_deleting, last))
            } finally {
                lastDeleted.postValue(null)
                parent.showLoading(false)
            }
        }
    }

    override fun selectReport(id: UUID) = showReport(id, false)

    override fun editReport(id: UUID) = showReport(id, true)

    private fun showReport(id: UUID, inEditMode: Boolean) {
        launch {
            parent.showLoading(true)
            try {
                val data = repo.loadReport(id).getOrThrow
                command.postValue(ReportListCommand.ShowReport(data, inEditMode))
            } catch (t: Throwable) {
                parent.showError(t.message ?: t::class.java.name)
            } finally {
                parent.showLoading(false)
            }
        }
    }

    override fun newReport() {
        val newReport = ReportData(EmptyUUID, created = Date())
        command.postValue(ReportListCommand.ShowReport(newReport, true))
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

}
