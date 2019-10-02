package gr.blackswamp.damagereports.logic

import androidx.annotation.VisibleForTesting
import gr.blackswamp.core.schedulers.IDispatchers
import gr.blackswamp.core.util.ILog
import gr.blackswamp.core.util.Log
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import gr.blackswamp.damagereports.ui.model.ReportHeader
import gr.blackswamp.damagereports.viewmodel.IReportViewModel
import gr.blackswamp.damagereports.viewmodel.ReportViewModel
import java.util.*
import kotlin.random.Random

class ReportListLogic(private val vm: IReportViewModel, private val db: IDatabase, private val dispatchers: IDispatchers, private val log: ILog = Log) : IReportLogic {
    companion object {
        private const val TAG = "ReportListLogic"
        @VisibleForTesting
        const val PAGE_SIZE = 20
    }

//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
//    val mFilter: BehaviorSubject<String> = BehaviorSubject.create()
//
//    private val mDisposable = CompositeDisposable()
//
//    override fun initialize() {
//        setListFilter("")
//
//        mFilter
//            .switchMap {
//                log.d(TAG, "filter changed: $it")
//                RxPagedListBuilder<Int, ReportHeaderEntity>(
//                    db.loadReportHeaders(it)
//                    , PagedList.Config.Builder()
//                        .setInitialLoadSizeHint(PAGE_SIZE)
//                        .setPageSize(PAGE_SIZE)
//                        .build()
//                )
//                    .setFetchScheduler(dispatchers.subscribeScheduler)
//                    .setNotifyScheduler(dispatchers.observeScheduler)
//                    .buildObservable()
//            }.map {
//                @Suppress("UNCHECKED_CAST")
//                it as PagedList<ReportHeader>
//            }
//            .subscribe({
//                log.d(TAG, "applying filter: \"${mFilter.value}\" with results ${it.size}")
//                vm.showReports(it)
//            }, {
//                vm.showError(R.string.error, it.localizedMessage)
//            }).addTo(mDisposable)
//    }
//
//    override fun setListFilter(filter: String) {
//        log.d(TAG, "changing filter $filter")
//        mFilter.onNext(filter)
//    }
//
//    override fun refresh() {
//        mFilter.onNext(mFilter.value)
//    }
//
//    override fun newReport() {
//        val rnd = Random(System.currentTimeMillis())
//        val now = System.currentTimeMillis()
//        val report = ReportEntity(
//            UUID.randomUUID(),
//            "name ${rnd.nextInt(100000)} ",
//            "description ${rnd.nextInt(100000)}",
//            UUID.randomUUID(),
//            UUID.randomUUID(),
//            Date(now - (rnd.nextInt(10) * 100000000)),
//            Date(now - (rnd.nextInt(10) * 100000000))
//        )
//
//        db.saveReport(report)
//            .doOnSubscribe {
//                vm.showLoading(true)
//            }
//            .doFinally {
//                vm.showLoading(false)
//            }
//            .subscribeOn(dispatchers.subscribeScheduler)
//            .observeOn(dispatchers.observeScheduler)
//            .subscribe({
//                log.i(ReportViewModel.TAG, "success")
//            }, {
//                vm.showError(R.string.error, it.localizedMessage)
//            }).addTo(mDisposable)
//
//    }
//
//    override fun deleteReport(id: UUID) {
//        db.deleteReportById(id)
//            .doOnSubscribe {
//                vm.showLoading(true)
//            }.doFinally {
//                vm.showLoading(false)
//            }.subscribeOn(dispatchers.subscribeScheduler)
//            .observeOn(dispatchers.observeScheduler)
//            .subscribe({
//                log.i(ReportViewModel.TAG, "success")
//            }, {
//                vm.showError(R.string.error, it.localizedMessage)
//            }).addTo(mDisposable)
//
//    }
//
//
//    override fun dispose() {
//        mDisposable.clear()
//    }
}
