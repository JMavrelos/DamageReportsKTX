package gr.blackswamp.damagereports.ui.reports

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.widget.visible
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.base.activities.BaseActivity
import gr.blackswamp.damagereports.ui.reports.fragments.ReportListFragment
import gr.blackswamp.damagereports.ui.reports.fragments.ReportViewFragment
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportActivityViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ReportActivity : BaseActivity<IReportActivityViewModel>() {
    override val layoutId: Int = R.layout.activity_report
    override val vm: IReportActivityViewModel by viewModel<ReportViewModel>()
    private lateinit var progress: View
    private var undo: Snackbar? = null

    override fun setUpBindings() {
        progress = findViewById(R.id.progress)
    }

    override fun initView(state: Bundle?) {
        if (state == null) {
            if (vm.report.value == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(
                        R.id.content,
                        ReportListFragment.newInstance(),
                        ReportListFragment.TAG
                    ).commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .add(
                        R.id.content,
                        ReportViewFragment.newInstance(),
                        ReportViewFragment.TAG
                    ).commit()
            }
        }
    }

    override fun setUpObservers(vm: IReportActivityViewModel) {
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
    }
}