package gr.blackswamp.damagereports.vms.base

import androidx.lifecycle.LiveData
import gr.blackswamp.core.vms.ICoreViewModel
import gr.blackswamp.damagereports.data.prefs.ThemeSetting

interface IBaseViewModel : ICoreViewModel {
    val themeSetting: LiveData<ThemeSetting>
}
