package gr.blackswamp.core.testing

import android.content.Context
import androidx.annotation.CallSuper
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4ClassRunner::class)
abstract class KoinAndroidTest : KoinTest {
    protected abstract val modules: Module
    protected val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    @CallSuper
    open fun setUp() {
        startKoin {
            androidContext(context)
            loadKoinModules(modules)
        }
    }

    @After
    @CallSuper
    open fun tearDown() {
        unloadKoinModules(modules)
        stopKoin()
    }
}