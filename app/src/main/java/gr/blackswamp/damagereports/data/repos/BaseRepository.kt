package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.data.prefs.ThemeMode

interface BaseRepository {
    val themeMode: ThemeMode
    val themeModeLive: LiveData<ThemeMode>
}