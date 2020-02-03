package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.data.prefs.ThemeSetting

interface BaseRepository {
    val themeSetting: ThemeSetting
    val themeSettingLive: LiveData<ThemeSetting>
}