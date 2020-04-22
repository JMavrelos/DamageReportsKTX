package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.repos.MainRepository
import org.koin.core.inject

class MainViewModelImpl(val app: Application) : CoreViewModel(app), MainViewModel {
    private val repo: MainRepository by inject()
    override val themeSetting: LiveData<ThemeSetting> = repo.themeSettingLive
    override val error = LiveEvent<String>()
    override val loading = MutableLiveData(false)
}

