package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.lifecycle.call
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.repos.MainRepository
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.MainViewModel
import org.koin.core.inject

class MainViewModelImpl(val app: Application) : CoreViewModel(app), MainViewModel, FragmentParent {
    private val repo: MainRepository by inject()
    override val themeSetting: LiveData<ThemeSetting> = repo.themeSettingLive
    override val error = LiveEvent<String>()
    override val loading = MutableLiveData(false)
    override val hideKeyboard = LiveEvent<Unit>()

    //region fragment parent
    override fun showError(message: String) = error.postValue(message)

    override fun showLoading(show: Boolean) = loading.postValue(show)
    override fun hideKeyboard() {
        hideKeyboard.call()
    }
    //endregion
}

