package gr.blackswamp.damagereports.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.core.widget.visible
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.databinding.ActivityMainBinding
import gr.blackswamp.damagereports.logic.interfaces.MainViewModel
import gr.blackswamp.damagereports.logic.vms.MainViewModelImpl
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : CoreActivity<MainViewModel, ActivityMainBinding>() {
    override val theme: Int? = R.style.AppTheme //sets the default application theme

    //region bindings
    override val vm: MainViewModel by viewModel<MainViewModelImpl>()
    override val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val progress: View by lazy { binding.progress }
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun initView(state: Bundle?) {
        setSupportActionBar(binding.toolbar)
        val navController = this.findNavController(R.id.container)
        appBarConfiguration = AppBarConfiguration(navController.graph) //create app bar configuration for navigation
        setupActionBarWithNavController(navController, appBarConfiguration) //, appBarConfiguration) //set up navigation for toolbar

    }

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
//        vm.back.observe { super.onBackPressed() }
        vm.error.observe { it?.let { showSnackBar(progress, it, Snackbar.LENGTH_LONG) } }
        vm.loading.observe { progress.visible = (it == true) }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.container).navigateUp(appBarConfiguration)
    }
}
