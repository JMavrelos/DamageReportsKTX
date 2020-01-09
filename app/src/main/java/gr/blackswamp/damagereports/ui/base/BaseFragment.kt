package gr.blackswamp.damagereports.ui.base

import gr.blackswamp.core.logging.ILog
import gr.blackswamp.core.ui.CoreFragment
import org.koin.android.ext.android.inject

abstract class BaseFragment<T : Any> : CoreFragment<T>() {
    protected val log: ILog by inject()
}