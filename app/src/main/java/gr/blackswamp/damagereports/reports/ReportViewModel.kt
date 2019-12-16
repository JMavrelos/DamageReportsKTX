package gr.blackswamp.damagereports.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.logging.ILog
import gr.blackswamp.damagereports.data.repos.IReportRepository
import java.util.*

class ReportViewModel(val repo: IReportRepository, application: Application, val dispatchers: IDispatchers, val log: ILog, runInit: Boolean = true) :
    AndroidViewModel(application) {


    companion object {
        const val TAG = "ReportViewModel"
    }

    init {
        if (runInit) {
        }
    }

    fun deleteReport(id: UUID) {

    }

    fun selectReport(id: UUID) {

    }

    fun newReport() {

    }

    fun newReportFilter(text: String, submitted: Boolean): Boolean {
        if (submitted) {
//            vm.newReportFilter(text)
            return true
        }
        return false
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