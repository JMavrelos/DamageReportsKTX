package gr.blackswamp.damagereports.ui.base.activities

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.Observer
import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.base.commands.ScreenCommand
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

abstract class BaseActivity<T : IBaseViewModel> : CoreActivity<T>() {
    override val theme: Int? = R.style.AppTheme
    override fun setUpObservers(vm: T) {
        vm.darkTheme.observe(this, Observer {
            it?.let {
                delegate.localNightMode = if (it) MODE_NIGHT_YES else MODE_NIGHT_NO
            }
        })
        vm.command.observe(this, Observer {
            if (!executeCommand(it)) {
                when (it) {
                    is ScreenCommand.HideKeyboard -> hideKeyboard()
                    is ScreenCommand.Back -> super.onBackPressed()
                }
            }
        })
    }

    protected open fun executeCommand(command: ScreenCommand): Boolean = false
}