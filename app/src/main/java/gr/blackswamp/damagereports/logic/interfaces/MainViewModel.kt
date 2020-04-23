package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import gr.blackswamp.core.vms.ICoreViewModel
import gr.blackswamp.damagereports.data.prefs.ThemeSetting

interface MainViewModel : ICoreViewModel {
    val themeSetting: LiveData<ThemeSetting>
    val error: LiveData<String>
    val loading: LiveData<Boolean>
}