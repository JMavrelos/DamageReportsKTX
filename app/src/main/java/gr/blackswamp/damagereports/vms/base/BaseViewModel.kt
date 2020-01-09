package gr.blackswamp.damagereports.vms.base

import android.app.Application
import gr.blackswamp.core.lifecycle.SingleLiveEvent
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.ui.base.ScreenCommand

abstract class BaseViewModel(app: Application) : CoreViewModel(app), IBaseViewModel {
    override val command = SingleLiveEvent<ScreenCommand>()
}