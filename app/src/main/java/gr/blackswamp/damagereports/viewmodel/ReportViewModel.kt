package gr.blackswamp.damagereports.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.core.lifecycle.SingleLiveEvent
import gr.blackswamp.core.schedulers.IDispatchers
import gr.blackswamp.damagereports.App
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.logic.ReportListLogic
import gr.blackswamp.damagereports.ui.fragments.ReportListFragment
import gr.blackswamp.damagereports.ui.model.ReportHeader
import java.util.*

class ReportViewModel(application: Application, db: IDatabase, dispatchers: IDispatchers) :
    AndroidViewModel(application)
    , IReportViewModel
    , ReportListFragment.ReportListViewModel {


    companion object {
        const val TAG = "ReportViewModel"
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