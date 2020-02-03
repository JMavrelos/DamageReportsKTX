package gr.blackswamp.damagereports.vms.models

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import gr.blackswamp.core.logging.ILog
import gr.blackswamp.damagereports.data.prefs.ThemeMode
import gr.blackswamp.damagereports.data.repos.ModelRepository
import gr.blackswamp.damagereports.vms.base.BaseViewModel
import org.koin.core.inject

class ModelViewModel(application: Application, runInit: Boolean) : BaseViewModel(application), ModelActivityViewModel {
    companion object {
        const val TAG = "ModelViewModel"
        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 30
    }
    private val log: ILog by inject()
    private val repo: ModelRepository by inject()
    override val themeMode: LiveData<ThemeMode> = repo.themeModeLive

    init {
        if (runInit) {
            initialize()
        }
    }

    @VisibleForTesting
    internal fun initialize() {
    }


}