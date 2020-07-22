package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.util.toThrowable
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.repos.ReportViewRepository
import gr.blackswamp.damagereports.logic.commands.ReportViewCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ReportViewViewModel
import gr.blackswamp.damagereports.logic.model.ReportData
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.model.ReportDamage
import kotlinx.coroutines.launch
import org.koin.core.inject
import timber.log.Timber

class ReportViewViewModelImpl(application: Application, parent: FragmentParent, report: Report, inEditMode: Boolean, runInit: Boolean = true) :
    BaseViewModel(application, parent), ReportViewViewModel {
    companion object {
        const val TAG = "ReportViewViewModel"

        @VisibleForTesting
        internal const val DAMAGES_PAGE_SIZE = 30
    }

    private val repo: ReportViewRepository by inject()

    //region live data
    override val report = MutableLiveData<Report>() // this is used for showing the changes to the current report
    override val command = LiveEvent<ReportViewCommand>()
    private val reportData get() = report.value as? ReportData
    override val damages: LiveData<PagedList<ReportDamage>> = this.report.switchMap(this::loadReportDamages)


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
            showError(t.message!!)
        }
    }


    //region IReportViewViewModel implementation

    private fun loadReportDamages(report: Report): LiveData<PagedList<ReportDamage>> {
        showLoading(true)
        return try {
            val response = repo.getDamageHeadersList(report.id)
            if (response.hasError) {
                showError(response.errorMessage)
                StaticDataSource.factory(listOf<ReportDamage>())
            } else {
                response.get.map {
                    Timber.d("Loaded $it")
                    it as ReportDamage
                }
            }.toLiveData(DAMAGES_PAGE_SIZE)
        } finally {
            showLoading(false)
//            refreshing.postValue(false)
        }
    }

    override fun pickBrand() {
        if (reportData == null) return
        command.postValue(ReportViewCommand.ShowBrandSelect)
    }

    override fun pickModel() {
        val current = reportData ?: return
        if (current.brand == null) {
            showError(getString(R.string.error_no_brand_selected))
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
        //todo:validations
        parent.hideKeyboard()
        launch {
            repo
        }
    }

    override fun editReport() {
        this.editMode.postValue(true)
    }

    override fun exitReport() {
        val report = report.value as? ReportData ?: return
        if (editMode.value == true && report.changed) { //we are editing a changed report
            command.postValue(ReportViewCommand.ConfirmDiscard)
        } else if (editMode.value == true && !report.changed && report.id != EmptyUUID) {
            editMode.postValue(false)
        } else {
            command.postValue(ReportViewCommand.MoveBack)
        }
    }

    override fun confirmDiscardChanges() {
        showLoading(true)
        launch {
            try {
                val report = report.value as? ReportData ?: return@launch
                if (report.id == EmptyUUID) {
                    command.postValue(ReportViewCommand.MoveBack)
                } else {
                    val org = repo.loadReport(report.id).getOrThrow
                    this@ReportViewViewModelImpl.report.postValue(org)
                    editMode.postValue(false)
                }
            } catch (t: Throwable) {
                showError(t.message ?: t::class.java.name)
            } finally {
                showLoading(false)
            }
        }

    }

    //endregion
}
