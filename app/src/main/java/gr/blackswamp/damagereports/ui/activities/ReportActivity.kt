package gr.blackswamp.damagereports.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.ActivityReportBinding
import gr.blackswamp.damagereports.ui.fragments.ReportListFragment
import gr.blackswamp.damagereports.viewmodel.ReportViewModel
import gr.blackswamp.damagereports.viewmodel.ViewModelFactory

class ReportActivity : AppCompatActivity(), IViewModelActivity {
    private lateinit var binding: ActivityReportBinding
    override lateinit var viewModel: ReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.content, ReportListFragment.newInstance(), ReportListFragment.TAG)
                .commit()
        }
        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(ReportViewModel::class.java)
//        viewModel.error.observe(this, Observer { showError(it) })
//        viewModel.loading.observe(this, Observer { binding.loading = it })
    }

    private fun showError(error: String?) {
        if (error == null) return
        val snackbar = Snackbar.make(binding.base, error, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.dismiss) { snackbar.dismiss() }.show()
    }
}