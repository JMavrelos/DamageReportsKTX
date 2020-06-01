package gr.blackswamp.core.testing

import android.app.Application
import androidx.annotation.CallSuper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import org.junit.After
import org.junit.Before
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock


abstract class KoinUnitTest : KoinTest {
    companion object {
        const val APP_STRING = "message"
    }

    protected abstract val modules: Module
    protected val app = mock(Application::class.java)

    @Before
    @CallSuper
    open fun setUp() {
        startKoin {
            androidContext(app)
            loadKoinModules(modules)
        }
        reset(app)
        setUpApplicationMocks()
    }

    private fun setUpApplicationMocks() {
        whenever(app.getString(anyInt())).thenReturn(APP_STRING)
        whenever(app.getString(anyInt(), any())).thenReturn(APP_STRING)
    }

    @After
    @CallSuper
    open fun tearDown() {
        unloadKoinModules(modules)
        stopKoin()
    }

}