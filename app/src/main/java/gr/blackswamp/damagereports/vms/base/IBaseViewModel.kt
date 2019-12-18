package gr.blackswamp.damagereports.vms.base

import androidx.lifecycle.LiveData

interface IBaseViewModel {
    val darkTheme: LiveData<Boolean>
}
