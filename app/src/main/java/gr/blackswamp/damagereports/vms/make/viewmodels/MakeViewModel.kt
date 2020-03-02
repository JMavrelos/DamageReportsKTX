package gr.blackswamp.damagereports.vms.make.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

interface MakeViewModel : IBaseViewModel {
    val error: LiveData<String>
    val loading: LiveData<Boolean>
}