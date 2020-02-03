package gr.blackswamp.damagereports.data.prefs

import androidx.lifecycle.LiveData

interface Preferences {
    var themeSetting: ThemeSetting
    val themeSettingLive: LiveData<ThemeSetting>
}
