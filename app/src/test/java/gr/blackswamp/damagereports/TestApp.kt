package gr.blackswamp.damagereports

import android.app.Application
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class TestApp : Application() {
    companion object {
        val app = Mockito.mock(TestApp::class.java)
        @JvmStatic
        fun initialize() {
            startKoin {
                androidContext(app)
                modules(emptyList())
            }
        }

        @JvmStatic
        fun dispose() {
            stopKoin()
        }

        fun startKoin(module: Module) {
            loadKoinModules(module)
        }

        fun stopKoin(module: Module) {
            unloadKoinModules(module)
        }

        fun withModules(module: Module, block: () -> Unit) {
            loadKoinModules(module)
            block()
            unloadKoinModules(module)
        }
    }
}