package gr.blackswamp.damagereports.vms.base

import androidx.lifecycle.LiveData
import gr.blackswamp.core.vms.ICoreViewModel
import gr.blackswamp.damagereports.data.prefs.ThemeMode

interface IBaseViewModel : ICoreViewModel {
    val themeMode: LiveData<ThemeMode>
}
