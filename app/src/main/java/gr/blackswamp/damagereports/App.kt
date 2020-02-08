package gr.blackswamp.damagereports

import android.app.Application
import gr.blackswamp.damagereports.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {
    companion object {
        const val PREFS_NAME = "DamageReportsPrefs"
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            if (BuildConfig.DEBUG)
                androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(applicationModule)
        }
    }
}