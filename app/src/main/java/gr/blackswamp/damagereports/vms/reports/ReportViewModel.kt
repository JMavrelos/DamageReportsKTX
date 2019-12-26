package gr.blackswamp.damagereports.vms.reports

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import gr.blackswamp.core.lifecycle.SingleLiveEvent
import gr.blackswamp.core.logging.ILog
import gr.blackswamp.damagereports.data.repos.IReportRepository
import gr.blackswamp.damagereports.ui.reports.model.Report
import gr.blackswamp.damagereports.ui.reports.model.ReportHeader
import gr.blackswamp.damagereports.vms.base.BaseViewModel
import gr.blackswamp.damagereports.vms.reports.model.ReportHeaderData
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportActivityViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportListViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportViewViewModel
import kotlinx.coroutines.launch
import java.util.*

class ReportViewModel(
    private val repo: IReportRepository,
    application: Application,
    private val log: ILog,
    runInit: Boolean = true
) :
    BaseViewModel(application), IReportActivityViewModel, IReportListViewModel,
    IReportViewViewModel {
    companion object {
        const val TAG = "ReportViewModel"
        private const val LIST_PAGE_SIZE = 30
    }

    private val filter = MutableLiveData<String>()
    override val darkTheme: LiveData<Boolean> = repo.darkThemeLive

    init {
        if (runInit) {
            filter.postValue("")
        }
    }

    //region IReportActivityViewModel implementation
    override val loading = MutableLiveData<Boolean>()
    override val error = SingleLiveEvent<String>()
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
    //endregion

    //region IReportListViewModel implementation

    override var reportHeaderList: LiveData<PagedList<ReportHeader>> =
        Transformations.switchMap(filter, this::dbHeaderToUi)

    override val refreshing = MutableLiveData<Boolean>()

    private fun dbHeaderToUi(filter: String?): LiveData<PagedList<ReportHeader>> {
        log.d(TAG, "transforming for $filter")
        val response = repo.getReportHeaders(filter ?: "")
        return if (response.hasError) {
            error.postValue(response.errorMessage)
            MutableLiveData()
        } else {
            response.get.map { entity ->
                log.d(TAG, "Loaded $entity")
                ReportHeaderData(entity) as ReportHeader
            }.toLiveData(pageSize = LIST_PAGE_SIZE)
        }
    }

    override fun deleteReport(id: UUID) {
        viewModelScope.launch {
            try {
                loading.postValue(true)
                repo.deleteReport(id)?.also { throw(it) }
            } catch (t: Throwable) {
                error.postValue(t.message ?: "Error deleting $id")
            } finally {
                loading.postValue(false)
            }
        }
    }

    override fun selectReport(id: UUID) {

    }

    override fun editReport(id: UUID) {

    }

    private var newRepId = 0
    override fun newReport() {
        viewModelScope.launch {
            val error = repo.newReport(
                "test ${newRepId++}",
                " desc ${newRepId}",
                UUID.randomUUID(),
                UUID.randomUUID()
            )
            if (error != null) {
                this@ReportViewModel.error.setValue(error.message!!)
            }
        }

    }

    override fun newReportFilter(filter: String, submitted: Boolean): Boolean {
//        if (submitted && this.filter.value != filter) {
        this.filter.postValue(filter)
        return true
//        }
//        return false
    }

    override fun reloadReports() {
        refreshing.postValue(true)
        this.filter.postValue(null)
        this.filter.postValue(this.filter.value)
        refreshing.postValue(false)
    }

    //endregion

    //region IReportViewViewModel implementation
    override fun pickModel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pickBrand() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveReport() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //endregion
}
