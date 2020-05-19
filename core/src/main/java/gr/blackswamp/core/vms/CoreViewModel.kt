package gr.blackswamp.core.vms

import android.app.Application
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.lifecycle.LiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

abstract class CoreViewModel(app: Application) : AndroidViewModel(app), ICoreViewModel, KoinComponent, CoroutineScope {
    private val supervisor = SupervisorJob()
    override val hideKeyboard = LiveEvent<Unit>()
    override val back = LiveEvent<Unit>()
    private val dispatchers: Dispatcher by inject()
    override val coroutineContext: CoroutineContext = supervisor + Dispatchers.Main.immediate


    protected fun getString(@StringRes resId: Int): String = getApplication<Application>().getString(resId)
    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String = getApplication<Application>().getString(resId, *formatArgs)

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        supervisor.cancel("View model cleared")
    }
}