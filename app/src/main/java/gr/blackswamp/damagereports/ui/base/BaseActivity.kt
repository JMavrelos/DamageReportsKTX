package gr.blackswamp.damagereports.ui.base

import androidx.appcompat.app.AppCompatDelegate.*
import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeMode
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

abstract class BaseActivity<T : IBaseViewModel> : CoreActivity<T>() {
    override val theme: Int? = R.style.AppTheme
    override fun setUpObservers(vm: T) {
        vm.themeMode.observe {
            when (it) {
                ThemeMode.System -> delegate.localNightMode = MODE_NIGHT_FOLLOW_SYSTEM
                ThemeMode.Dark -> delegate.localNightMode = MODE_NIGHT_YES
                ThemeMode.Light -> delegate.localNightMode = MODE_NIGHT_NO
                ThemeMode.Auto -> delegate.localNightMode = MODE_NIGHT_AUTO_BATTERY

            }
        }
        vm.hideKeyboard.observe { hideKeyboard() }
        vm.back.observe { super.onBackPressed() }
    }
}