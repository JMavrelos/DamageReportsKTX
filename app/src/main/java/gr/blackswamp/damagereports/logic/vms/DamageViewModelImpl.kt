package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.logic.interfaces.DamageViewModel

class DamageViewModelImpl(app: Application) : CoreViewModel(app), DamageViewModel