package gr.blackswamp.damagereports.vms.brands

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import gr.blackswamp.core.logging.ILog
import gr.blackswamp.damagereports.data.prefs.ThemeMode
import gr.blackswamp.damagereports.data.repos.BrandRepository
import gr.blackswamp.damagereports.vms.base.BaseViewModel
import org.koin.core.inject

class BrandViewModel(application: Application, runInit:Boolean) : BaseViewModel(application), BrandActivityViewModel {
    companion object {
        const val TAG = "BrandViewModel"
        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 30
    }
    private val log: ILog by inject()
    private val repo: BrandRepository by inject()
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