package gr.blackswamp.damagereports

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        lateinit var application: App
            private set
        val context: Context
            get() = application.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}