package gr.blackswamp.core.vms

import androidx.lifecycle.LiveData

interface ICoreViewModel {
    val hideKeyboard: LiveData<Unit>
    val back: LiveData<Unit>
}
