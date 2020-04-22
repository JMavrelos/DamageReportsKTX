package gr.blackswamp.damagereports.data.repos

import android.app.Application
import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainRepositoryImpl : MainRepository, KoinComponent {
    private val prefs: Preferences by inject()
    private val application: Application by inject()
    override val themeSettingLive: LiveData<ThemeSetting> = prefs.themeSettingLive
}