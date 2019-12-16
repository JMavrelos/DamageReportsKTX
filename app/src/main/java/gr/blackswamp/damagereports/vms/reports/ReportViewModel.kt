package gr.blackswamp.damagereports.vms.reports

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.lifecycle.SingleLiveEvent
import gr.blackswamp.core.logging.ILog
import gr.blackswamp.damagereports.data.repos.IReportRepository
import gr.blackswamp.damagereports.ui.reports.commands.ReportListCommand
import gr.blackswamp.damagereports.vms.reports.model.ReportHeaderData
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportActivityViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportListViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.CancellationException

class ReportViewModel(private val repo: IReportRepository, application: Application, private val dispatchers: IDispatchers, private val log: ILog) :
    AndroidViewModel(application), IReportActivityViewModel, IReportListViewModel {
    companion object {
        const val NEW_QUERY_CANCELLATION = "new_query"
        const val TAG = "ReportViewModel"
    }

    private var mLoadJob: Job? = null
    private var filter: String = ""
    override val reportListCommands = SingleLiveEvent<ReportListCommand>()
    override val error = SingleLiveEvent<String>()

    init {
        loadReports("", 0)
    }

    //region IReportListViewModel implementation
    override fun deleteReport(id: UUID) {

    }

    override fun selectReport(id: UUID) {

    }

    override fun newReport() {

    }

    override fun newReportFilter(filter: String, submitted: Boolean): Boolean {
        if (submitted) {
            loadReports(filter, 0)
            return true
        }
        return false
    }

    override fun reloadReports() {
        loadReports("", 0)
    }

    override fun loadNextReports(current: Int) {
        loadReports(filter, current)
    }
    //endregion

    @VisibleForTesting
    internal fun loadReports(newFilter: String, skip: Int) {
        mLoadJob?.cancel(CancellationException(NEW_QUERY_CANCELLATION))
        mLoadJob = viewModelScope.launch {
            try {
                val result =
                    if (newFilter.equals(filter, true)) { //if the filter has not changed we obey the skip
                        repo.loadReports(filter, skip)
                    } else {
                        repo.loadReports(filter, 0)
                    }
                if (result.hasError)
                    throw (result.error)
                if (skip == 0) {
                    reportListCommands.setValue(ReportListCommand.SetReports(result.get.map { ReportHeaderData(it) }))
                } else {
                    reportListCommands.setValue(ReportListCommand.AddReports(result.get.map { ReportHeaderData(it) }))
                }

            } catch (t: Throwable) {
                if (t is CancellationException && t.message == NEW_QUERY_CANCELLATION) {
                    return@launch
                }
            }
        }
    }


//    private val logic = ReportListLogic(this, db, dispatchers)
//    private val mDisposables = CompositeDisposable()
//    override val reports = MutableLiveData<PagedList<ReportHeader>>()
//    val loading = MutableLiveData<Boolean>()
//    val error = SingleLiveEvent<String>()
//
//    init {
//        logic.initialize()
//    }
//
//
//    //region incoming from logic
//    override fun showError(resId: Int, vararg prms: String) {
//        error.postValue(getApplication<App>().applicationContext.getString(resId, prms))
//    }
//
//    override fun showLoading(show: Boolean) =
//        loading.postValue(show)
//
//    override fun showReports(reports: PagedList<ReportHeader>) {
//        this.reports.postValue(reports)
//    }
//
//    //endregion
//
//
//    //region incoming from report list
//    override fun deleteReport(id: UUID) =
//        logic.deleteReport(id)
//
//    override fun selectReport(id: UUID) =
//        error.postValue("Selected $id")
//
//    override fun newReport() =
//        logic.newReport()
//
//    override fun newReportListFilter(text: String) =
//        logic.setListFilter(text)
//
//    override fun refresh() {
//        logic.refresh()
//    }
//    //endregion
//
//    override fun onCleared() {
//        super.onCleared()
//        mDisposables.clear()
//    }
}