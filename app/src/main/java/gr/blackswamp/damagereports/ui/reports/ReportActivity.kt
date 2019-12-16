package gr.blackswamp.damagereports.ui.reports

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.reports.fragments.ReportListFragment
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportActivityViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ReportActivity : CoreActivity<IReportActivityViewModel>() {
    override val layoutId: Int = R.layout.activity_report
    override val vm: IReportActivityViewModel by viewModel<ReportViewModel>()
    private lateinit var mProgress: View

    override fun setUpBindings() {
        mProgress = findViewById(R.id.progress)
    }


    override fun initView(state: Bundle?) {
        if (state == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.content,
                    ReportListFragment.newInstance(),
                    ReportListFragment.TAG
                )
                .commit()
        }
    }

    override fun setUpObservers(vm: IReportActivityViewModel) {
        vm.error.observe(this, Observer {
            it?.let {
                Snackbar.make(mProgress, it, Snackbar.LENGTH_LONG).apply {
                    setAction(R.string.dismiss) { this.dismiss() }
                    show()
                }
            }
        })
    }


    private fun showError(error: String?) {
        if (error == null) return
//        val snackbar = Snackbar.make(
//                binding.base,
//                error,
//                Snackbar.LENGTH_INDEFINITE
//        )
//        snackbar.setAction(R.string.dismiss) { snackbar.dismiss() }.show()
    }
}