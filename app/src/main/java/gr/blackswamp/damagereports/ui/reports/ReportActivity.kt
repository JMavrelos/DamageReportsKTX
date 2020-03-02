package gr.blackswamp.damagereports.ui.reports

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.dialogs.Dialog.Companion.BUTTON_POSITIVE
import gr.blackswamp.core.dialogs.DialogBuilders
import gr.blackswamp.core.dialogs.DialogFinishedListener
import gr.blackswamp.core.widget.visible
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.ActivityReportBinding
import gr.blackswamp.damagereports.ui.base.BaseActivity
import gr.blackswamp.damagereports.ui.make.MakeActivity
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.reports.commands.ReportActivityCommand
import gr.blackswamp.damagereports.ui.reports.fragments.ReportListFragment
import gr.blackswamp.damagereports.ui.reports.fragments.ReportViewFragment
import gr.blackswamp.damagereports.vms.reports.ReportViewModelImpl
import gr.blackswamp.damagereports.vms.reports.viewmodels.ReportViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class ReportActivity : BaseActivity<ReportViewModel, ActivityReportBinding>(), DialogFinishedListener {
    companion object {
        private const val TAG = "ReportActivity"
        private const val SHOW_REPORT = "show_report"
        private const val DISCARD_CONFIRM_ID = 39012
    }

    //region bindings
    override val vm: ReportViewModel by viewModel<ReportViewModelImpl>()
    override val binding: ActivityReportBinding by lazy { ActivityReportBinding.inflate(layoutInflater) }
    private var undo: Snackbar? = null
    private val progress: View by lazy { binding.progress }
    //endregion

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

    override fun setUpObservers(vm: ReportViewModel) {
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
            else -> true
        }
    }


    override fun onBackPressed() {
        vm.backPressed()
    }
    //endregion

    //region event handlers

    private fun executeCommand(cmd: ReportActivityCommand?) {
        when (cmd) {
            is ReportActivityCommand.ShowBrandSelection -> gotoBrandSelect()
            is ReportActivityCommand.ShowModelSelection -> gotoModelSelect(cmd.brandId)
            is ReportActivityCommand.ConfirmDiscard -> showDiscardConfirmDialog()
        }
    }

    //endregion

    //region private code
    private fun showUndoLastDelete() {
        //create a snack bar with an undo message
        undo = Snackbar.make(progress, R.string.undo_delete, Snackbar.LENGTH_LONG).apply {
            //attach an action to undo the last deletion
            this.setAction(R.string.undo) { vm.undoLastDelete() }
            //and a callback that will clear the last deleted value when the snack bar is dismissed
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
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
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
        startActivity(MakeActivity.getIntent(this, null))
    }

    private fun gotoModelSelect(brandId: UUID) {
        startActivity(MakeActivity.getIntent(this, brandId))
    }
    //endregion

}