package gr.blackswamp.damagereports.ui.reports.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportViewViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ReportViewFragment : CoreFragment<IReportViewViewModel>() {
    companion object {
        const val TAG = "ReportViewFragment"
        fun newInstance(): Fragment = ReportViewFragment()
    }

    override val vm: IReportViewViewModel by sharedViewModel<ReportViewModel>()
    override val layoutId: Int = R.layout.fragment_report_view


    //region bindings
    private lateinit var mToolbar: Toolbar
    private lateinit var mId: TextView
    private lateinit var mName: EditText
    private lateinit var mDescription: EditText
    private lateinit var mModel: Button
    private lateinit var mBrand: Button
    private lateinit var mRefreshDamages: SwipeRefreshLayout
    private lateinit var mDamages: RecyclerView
    private lateinit var mAction: Button
    private lateinit var mSave: MenuItem
    //endregion

    override fun setUpBindings(view: View) {
        mToolbar = view.findViewById(R.id.toolbar)
        mSave = mToolbar.menu.findItem(R.id.save)
        mId = view.findViewById(R.id.id)
        mName = view.findViewById(R.id.name)
        mDescription = view.findViewById(R.id.description)
        mModel = view.findViewById(R.id.model)
        mBrand = view.findViewById(R.id.brand)
        mRefreshDamages = view.findViewById(R.id.refresh)
        mDamages = view.findViewById(R.id.damages)
        mAction = view.findViewById(R.id.action)
    }

    override fun initView(state: Bundle?) {

    }

    override fun setUpListeners() {
        mModel.setOnClickListener { vm.pickModel() }
        mBrand.setOnClickListener { vm.pickBrand() }
        mSave.setOnMenuItemClickListener { vm.saveReport(); true }
    }

    override fun setUpObservers(vm: IReportViewViewModel) {
        vm.report.observe(this, Observer { updateView(it) })
    }

    private fun updateView(report: Report?) {
        if (report == null) return


    }
}