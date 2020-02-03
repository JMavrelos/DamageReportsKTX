package gr.blackswamp.damagereports.data.repos

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseRepositoryImpl : BaseRepository, KoinComponent {
    protected val prefs: Preferences by inject()
    private val application: Application by inject()

    override val themeSetting: ThemeSetting
        get() = prefs.themeSetting

    override val themeSettingLive: LiveData<ThemeSetting> = prefs.themeSettingLive

    protected fun getString(@StringRes resId: Int): String = application.getString(resId)
    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String = application.getString(resId, *formatArgs)
}