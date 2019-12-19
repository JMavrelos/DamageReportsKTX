package gr.blackswamp.damagereports.ui.reports

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
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
    private lateinit var mProgress: View

    override fun setUpBindings() {
        mProgress = findViewById(R.id.progress)
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
        vm.error.observe(this, Observer { it?.let { showSnackBar(mProgress, it, Snackbar.LENGTH_LONG) } })
        vm.loading.observe(this, Observer { mProgress.visible = (it == true) })
    }
}