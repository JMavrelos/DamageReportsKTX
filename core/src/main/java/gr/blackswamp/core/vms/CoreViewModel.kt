package gr.blackswamp.core.vms

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import gr.blackswamp.core.lifecycle.SingleLiveEvent
import org.koin.core.KoinComponent

abstract class CoreViewModel(app: Application) : AndroidViewModel(app), ICoreViewModel, KoinComponent {
    override val hideKeyboard = SingleLiveEvent<Unit>()
    override val back = SingleLiveEvent<Unit>()

    protected fun getString(@StringRes resId: Int): String = getApplication<Application>().getString(resId)
    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String = getApplication<Application>().getString(resId, *formatArgs)


}