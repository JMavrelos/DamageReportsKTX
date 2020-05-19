package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent

abstract class BaseViewModel(application: Application, private val parent: FragmentParent) : CoreViewModel(application) {
    protected fun showLoading(show: Boolean) = parent.showLoading(show)
    protected fun hideKeyboard() = parent.hideKeyboard()
    protected fun showError(message: String) = parent.showError(message)
}