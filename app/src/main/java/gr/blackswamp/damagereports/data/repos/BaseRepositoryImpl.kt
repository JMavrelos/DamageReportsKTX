package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.data.prefs.ThemeMode
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseRepositoryImpl : BaseRepository, KoinComponent {
    protected val prefs: Preferences by inject()

    override val themeMode: ThemeMode
        get() = prefs.themeMode

    override val themeModeLive: LiveData<ThemeMode> = prefs.themeModeLive
}