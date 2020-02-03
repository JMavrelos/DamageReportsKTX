package gr.blackswamp.damagereports.ui.reports

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.dialogs.Dialog
import gr.blackswamp.core.dialogs.Dialog.Companion.BUTTON_POSITIVE
import gr.blackswamp.core.dialogs.DialogBuilders
import gr.blackswamp.core.dialogs.DialogFinishedListener
import gr.blackswamp.core.widget.visible
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.ui.base.BaseActivity
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.reports.fragments.ReportListFragment
import gr.blackswamp.damagereports.ui.reports.fragments.ReportViewFragment
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.ReportActivityViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ReportActivity : BaseActivity<ReportActivityViewModel>(), DialogFinishedListener {
    companion object {
        private const val TAG = "ReportActivity"
        private const val SHOW_REPORT = "show_report"
        private const val DISCARD_CONFIRM_ID = 39012
        private const val THEME_SELECTION_ID = 3022
    }

    override val layoutId: Int = R.layout.activity_report
    override val vm: ReportActivityViewModel by viewModel<ReportViewModel>()
    private lateinit var progress: View
    private var undo: Snackbar? = null

    //region lifecycle methods
    override fun setUpBindings() {
        progress = findViewById(R.id.progress)
    }

    override fun initView(state: Bundle?) {
        if (state == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.content,
                    ReportListFragment.newInstance(),
                    ReportListFragment.TAG
                ).commit()
        }
    }

    override fun setUpObservers(vm: ReportActivityViewModel) {
        super.setUpObservers(vm)
        vm.error.observe { it?.let { showSnackBar(progress, it, Snackbar.LENGTH_LONG) } }
        vm.loading.observe { progress.visible = (it == true) }
        vm.report.observe(this::showReport)
        vm.showUndo.observe { showUndoLastDelete() }
        vm.activityCommand.observe(this::executeCommand)
    }

    override fun dialogFinished(id: Int, which: Int, dialog: View, payload: Bundle?): Boolean {
        return when (id) {
            DISCARD_CONFIRM_ID -> {
                //on cancel
                if (which == BUTTON_POSITIVE) {
                    vm.confirmDiscardChanges()
                }
                true
            }
            THEME_SELECTION_ID -> {
                if (which == BUTTON_POSITIVE) {
                    val themeMode = when ((dialog as RadioGroup).checkedRadioButtonId) {
                        R.id.dark -> ThemeSetting.Dark
                        R.id.light -> ThemeSetting.Light
                        R.id.auto -> ThemeSetting.Auto
                        else -> ThemeSetting.System
                    }
                    vm.changeTheme(themeMode)
                }
                true
            }
            else -> true
        }
    }


    override fun onBackPressed() {
        vm.backPressed()
    }
    //endregion

    //region event handlers

    private fun executeCommand(cmd: ReportCommand?) {
        when (cmd) {
            is ReportCommand.ShowBrandSelection -> gotoBrandSelect()
            is ReportCommand.ShowModelSelection -> gotoModelSelect(cmd.brandId)
            is ReportCommand.ConfirmDiscard -> showDiscardConfirmDialog()
            is ReportCommand.ShowThemeSelection -> showThemeSelectionDialog(cmd.current)
        }

    }

    //endregion

    //region private code
    private fun showUndoLastDelete() {
        //create a snack bar with an undo message
        undo = Snackbar.make(progress, R.string.undo_delete, Snackbar.LENGTH_LONG).apply {
            //attach an action to undo the last deletion
            this.setAction(R.string.undo) { vm.undoLastDelete() }
            //and a callback that will clear the last deleted value when the snackbar is dismissed
            this.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    vm.dismissedUndo()
                }
            })
            this.show()
        }
    }


    private fun showReport(report: Report?) {
        if (report != null) {
            if (supportFragmentManager.backStackEntryCount == 0) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content, ReportViewFragment.newInstance(), ReportViewFragment.TAG)
                    .addToBackStack(SHOW_REPORT)
                    .commit()
            }
        } else {
            supportFragmentManager.popBackStackImmediate(SHOW_REPORT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun showDiscardConfirmDialog() {
        DialogBuilders
            .messageDialogBuilder(DISCARD_CONFIRM_ID, getString(R.string.confirm_discard_changes))
            .setCancelable(true)
            .setButtons(positive = true, negative = true, neutral = true)
            .show(this)
    }

    private fun gotoBrandSelect() {

    }

    private fun gotoModelSelect(brandId: UUID) {

    }

    private fun showThemeSelectionDialog(current: ThemeSetting) {
        Dialog.builder(THEME_SELECTION_ID, R.layout.dialog_theme)
            .setCancelable(true)
            .setTitle(getString(R.string.select_theme))
            .setButtons(positive = true, negative = true, neutral = false)
            .setInitViewCallback {
                val id = when (current) {
                    ThemeSetting.Dark -> R.id.dark
                    ThemeSetting.System -> R.id.system
                    ThemeSetting.Auto -> R.id.auto
                    ThemeSetting.Light -> R.id.light
                }
                (it as RadioGroup).check(id)
            }
            .show(this)
    }
    //endregion

}