package gr.blackswamp.damagereports.data.prefs

import androidx.lifecycle.LiveData

interface IPreferences {
    var darkTheme: Boolean
    val darkThemeLive: LiveData<Boolean>
}
