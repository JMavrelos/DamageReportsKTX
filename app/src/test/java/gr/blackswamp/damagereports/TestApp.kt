package gr.blackswamp.damagereports

import android.app.Application
import gr.blackswamp.core.TestDispatchers
import gr.blackswamp.core.TestLog
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.logging.ILog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

@ExperimentalCoroutinesApi
class TestApp : Application() {
    override fun onCreate() {
        super.onCreate()
//        startKoin {
//            androidLogger()
//            androidContext(this@TestApp)
//            modules(emptyList())
//        }
    }

    fun loadModules(module: Module, block: () -> Unit) {
        loadKoinModules(module)
        block()
        unloadKoinModules(module)
    }
}