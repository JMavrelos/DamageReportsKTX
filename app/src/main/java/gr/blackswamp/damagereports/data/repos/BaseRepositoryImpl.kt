package gr.blackswamp.damagereports.data.repos

import android.app.Application
import androidx.annotation.StringRes
import gr.blackswamp.damagereports.data.prefs.Preferences
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseRepositoryImpl : BaseRepository, KoinComponent {
    protected val prefs: Preferences by inject()
    private val application: Application by inject()
    protected fun getString(@StringRes resId: Int): String = application.getString(resId)
    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String = application.getString(resId, *formatArgs)
}