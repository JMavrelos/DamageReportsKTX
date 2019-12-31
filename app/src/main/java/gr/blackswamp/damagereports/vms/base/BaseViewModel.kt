package gr.blackswamp.damagereports.vms.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.koin.core.KoinComponent

abstract class BaseViewModel(app: Application) : AndroidViewModel(app) , IBaseViewModel,
    KoinComponent {
}