package gr.blackswamp.damagereports.vms.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class BaseViewModel(app: Application) : AndroidViewModel(app) , IBaseViewModel{
}