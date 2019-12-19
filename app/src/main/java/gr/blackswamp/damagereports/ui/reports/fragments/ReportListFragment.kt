package gr.blackswamp.damagereports.ui.reports.fragments


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.core.widget.CItemTouchHelperCallback
import gr.blackswamp.core.widget.SearchListener
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.reports.adapters.ReportListAction
import gr.blackswamp.damagereports.ui.reports.adapters.ReportListAdapter
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportListViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*


class ReportListFragment : CoreFragment<IReportListViewModel>(), ReportListAction {
    companion object {
        const val TAG = "ReportListFragment"
        fun newInstance(): Fragment = ReportListFragment()
    }


    override val vm: IReportListViewModel by sharedViewModel<ReportViewModel>()
    override val layoutId: Int = R.layout.fragment_report_list

    private lateinit var mRefresh: SwipeRefreshLayout
    private lateinit var mAdd: FloatingActionButton
    private lateinit var mList: RecyclerView
    private lateinit var mAdapter: ReportListAdapter
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mTheme: MenuItem

    override fun setUpBindings(view: View) {
        mRefresh = view.findViewById(R.id.refresh)
        mAdd = view.findViewById(R.id.add)
        mList = view.findViewById(R.id.list)
        mAdapter = ReportListAdapter()
        mToolbar = view.findViewById(R.id.toolbar)
        mTheme = mToolbar.menu.findItem(R.id.switch_theme)
    }

    override fun initView(state: Bundle?) {
        mList.adapter = mAdapter
        ItemTouchHelper(CItemTouchHelperCallback(mAdapter, allowSwipe = true, allowDrag = false)).attachToRecyclerView(mList)
    }

    override fun setUpListeners() {
        mAdapter.setListener(this)
        mAdd.setOnClickListener { vm.newReport() }
        mRefresh.setOnRefreshListener { vm.reloadReports() }
        (mToolbar.menu?.findItem(R.id.search_reports)?.actionView as? SearchView)?.setOnQueryTextListener(SearchListener(vm::newReportFilter))
        mToolbar.menu?.findItem(R.id.switch_theme)?.setOnMenuItemClickListener { vm.toggleTheme();true }
        mToolbar.setNavigationOnClickListener { Toast.makeText(activity!!, "BACK", Toast.LENGTH_LONG).show() }
    }

    override fun setUpObservers(vm: IReportListViewModel) {
        vm.reportHeaderList.observe(this, Observer { mAdapter.submitList(it) })
        vm.refreshing.observe(this, Observer {
            if (it == false)
                mRefresh.isRefreshing = false
        })
        vm.darkTheme.observe(this, Observer {
            if (it == true) {
                mTheme.setTitle(R.string.switch_to_light)
                mTheme.setIcon(R.drawable.ic_brightness_7_on_control)
            } else {
                mTheme.setTitle(R.string.switch_to_dark)
                mTheme.setIcon(R.drawable.ic_brightness_4_on_control)
            }
        })
    }

    override fun delete(id: UUID) = vm.deleteReport(id)

    override fun select(id: UUID) = vm.selectReport(id)

    override fun edit(id: UUID) = vm.editReport(id)
}