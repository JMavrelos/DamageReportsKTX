package gr.blackswamp.damagereports.vms.make

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.repos.MakeRepository
import gr.blackswamp.damagereports.vms.base.BaseViewModel
import gr.blackswamp.damagereports.vms.make.viewmodels.BrandParent
import gr.blackswamp.damagereports.vms.make.viewmodels.MakeViewModel
import gr.blackswamp.damagereports.vms.make.viewmodels.ModelParent
import org.koin.core.inject
import timber.log.Timber
import java.util.*

class MakeViewModelImpl(application: Application, val brandId: UUID?) :
    BaseViewModel(application)
    , MakeViewModel
    , BrandParent
    , ModelParent {

    private val repo: MakeRepository by inject()
    private val dispatchers: IDispatchers by inject()

    companion object {
        const val TAG = "MakeViewModel"
        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 100
    }

    override val themeSetting: LiveData<ThemeSetting> = repo.themeSettingLive

    init {
        Timber.d(TAG, "Brand id $brandId")
    }

}