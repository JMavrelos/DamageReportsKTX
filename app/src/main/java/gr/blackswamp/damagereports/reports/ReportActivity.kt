package gr.blackswamp.damagereports.reports

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.reports.fragments.ReportListFragment
import org.koin.android.viewmodel.ext.android.viewModel

class ReportActivity : CoreActivity<ReportViewModel>() {
    override val layoutId: Int = R.layout.activity_report
    override val vm: ReportViewModel by viewModel()

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