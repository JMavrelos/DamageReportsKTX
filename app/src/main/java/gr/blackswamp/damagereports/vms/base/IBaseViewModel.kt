package gr.blackswamp.damagereports.vms.base

import androidx.lifecycle.LiveData
import gr.blackswamp.core.vms.ICoreViewModel
import gr.blackswamp.damagereports.ui.base.commands.ScreenCommand

interface IBaseViewModel : ICoreViewModel {
    val darkTheme: LiveData<Boolean>
    val command: LiveData<ScreenCommand>
}
