package gr.blackswamp.damagereports

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import gr.blackswamp.core.testing.TestTree
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import timber.log.Timber

@ExperimentalCoroutinesApi
class TestApp : Application() {
    companion object {
        fun startKoin(module: Module) {
            startKoin {
                androidContext(ApplicationProvider.getApplicationContext())
                modules(emptyList())
            }
            loadKoinModules(module)
        }

        fun stopKoin(module: Module) {
            unloadKoinModules(module)
            stopKoin()
        }

        fun withModules(module: Module, block: () -> Unit) {
            startKoin {
                androidContext(ApplicationProvider.getApplicationContext())
                modules(emptyList())
            }
            loadKoinModules(module)
            block()
            unloadKoinModules(module)
            stopKoin()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(TestTree())
    }
}