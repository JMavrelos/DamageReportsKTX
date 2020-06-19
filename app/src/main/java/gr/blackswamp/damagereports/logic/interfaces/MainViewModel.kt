package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.data.prefs.ThemeSetting

interface MainViewModel {
    val themeSetting: LiveData<ThemeSetting>
    val error: LiveData<String>
    val loading: LiveData<Boolean>
    val hideKeyboard: LiveData<Unit>
}