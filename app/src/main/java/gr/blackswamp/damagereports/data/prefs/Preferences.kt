package gr.blackswamp.damagereports.data.prefs

import androidx.lifecycle.LiveData

interface Preferences {
    var themeMode: ThemeMode
    val themeModeLive: LiveData<ThemeMode>
}
