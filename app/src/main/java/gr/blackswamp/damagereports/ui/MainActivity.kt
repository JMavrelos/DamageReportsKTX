package gr.blackswamp.damagereports.ui

import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.core.widget.visible
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.databinding.ActivityMainBinding
import gr.blackswamp.damagereports.logic.vms.MainViewModel
import gr.blackswamp.damagereports.logic.vms.MainViewModelImpl
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : CoreActivity<MainViewModel>() {
    override val theme: Int? = R.style.AppTheme //sets the default application theme

    //region bindings
    override val vm: MainViewModel by viewModel<MainViewModelImpl>()
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val progress: View by lazy { binding.progress }
    private var undo: Snackbar? = null
    //endregion

    override fun setUpObservers(vm: MainViewModel) {
        vm.themeSetting.observe {
            when (it) {
                ThemeSetting.System -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                ThemeSetting.Dark -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                ThemeSetting.Light -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                ThemeSetting.Auto -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY

            }
        }
        vm.hideKeyboard.observe { hideKeyboard() }
        vm.back.observe { super.onBackPressed() }
        vm.error.observe { it?.let { showSnackBar(progress, it, Snackbar.LENGTH_LONG) } }
        vm.loading.observe { progress.visible = (it == true) }
    }

    override fun setUpBindings() {
        setContentView(binding.root)
    }
}
