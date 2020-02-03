package gr.blackswamp.damagereports.ui.base

import androidx.appcompat.app.AppCompatDelegate.*
import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.vms.base.IBaseViewModel

abstract class BaseActivity<T : IBaseViewModel> : CoreActivity<T>() {
    override val theme: Int? = R.style.AppTheme
    override fun setUpObservers(vm: T) {
        vm.themeSetting.observe {
            when (it) {
                ThemeSetting.System -> delegate.localNightMode = MODE_NIGHT_FOLLOW_SYSTEM
                ThemeSetting.Dark -> delegate.localNightMode = MODE_NIGHT_YES
                ThemeSetting.Light -> delegate.localNightMode = MODE_NIGHT_NO
                ThemeSetting.Auto -> delegate.localNightMode = MODE_NIGHT_AUTO_BATTERY

            }
        }
        vm.hideKeyboard.observe { hideKeyboard() }
        vm.back.observe { super.onBackPressed() }
    }
}