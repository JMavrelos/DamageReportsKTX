package gr.blackswamp.damagereports.vms.base

import android.app.Application
import gr.blackswamp.core.vms.CoreViewModel

abstract class BaseViewModel(app: Application) : CoreViewModel(app), IBaseViewModel