package gr.blackswamp.damagereports.ui.reports

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.dialogs.Dialog.Companion.BUTTON_POSITIVE
import gr.blackswamp.core.dialogs.DialogBuilders
import gr.blackswamp.core.dialogs.DialogFinishedListener
import gr.blackswamp.core.widget.visible
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.base.BaseActivity
import gr.blackswamp.damagereports.ui.base.ScreenCommand
import gr.blackswamp.damagereports.ui.reports.fragments.ReportListFragment
import gr.blackswamp.damagereports.ui.reports.fragments.ReportViewFragment
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.ReportActivityViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ReportActivity : BaseActivity<ReportActivityViewModel>(), DialogFinishedListener {
    companion object {
        private const val TAG = "ReportActivity"
        private const val SHOW_REPORT = "show_report"
        private const val DISCARD_CONFIRM_ID = 39012
    }

    override val layoutId: Int = R.layout.activity_report
    override val vm: ReportActivityViewModel by viewModel<ReportViewModel>()
    internal lateinit var progress: View
    private var undo: Snackbar? = null

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
        vm.error.observe(this, Observer { it?.let { showSnackBar(progress, it, Snackbar.LENGTH_LONG) } })
        vm.loading.observe(this, Observer { progress.visible = (it == true) })
        vm.showUndo.observe(this, Observer {
            if (it == true) { //if we actually have a value
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
        })
        vm.report.observe(this, Observer {
            if (it != null) {
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
        })
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
            else -> true
        }
    }

    override fun onBackPressed() {
        vm.backPressed()
    }

    override fun executeCommand(command: ScreenCommand): Boolean {
        return when (command) {
            is ReportCommand.ConfirmDiscard -> {
                DialogBuilders
                    .messageDialogBuilder(DISCARD_CONFIRM_ID, getString(R.string.confirm_discard_changes))
                    .setCancelable(true)
                    .setButtons(positive = true, negative = true, neutral = true)
                    .show(this)
                true
            }
            is ReportCommand.ShowModelSelection -> {

                true
            }
            is ReportCommand.ShowBrandSelection -> {
                true
            }
            else -> false
        }
    }
}