package gr.blackswamp.damagereports.data.prefs

import androidx.lifecycle.LiveData

interface Preferences {
    var darkTheme: Boolean
    val darkThemeLive: LiveData<Boolean>
}
