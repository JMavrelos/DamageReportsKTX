package gr.blackswamp.damagereports.ui.base.activities

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.Observer
import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

abstract class BaseActivity<T : IBaseViewModel> : CoreActivity<T>() {
    override fun setUpObservers(vm: T) {
        vm.darkTheme.observe(this, Observer {
            it?.let {
                delegate.localNightMode = if (it) MODE_NIGHT_YES else MODE_NIGHT_NO
            }
        })
    }
}