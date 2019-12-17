package gr.blackswamp.damagereports.ui.reports.fragments


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.core.widget.CItemTouchHelperCallback
import gr.blackswamp.core.widget.SearchListener
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.reports.adapters.ReportListAdapter
import gr.blackswamp.damagereports.ui.reports.adapters.onReportListAction
import gr.blackswamp.damagereports.ui.reports.commands.ReportListCommand
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportListViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*


class ReportListFragment : CoreFragment<IReportListViewModel>(), onReportListAction {
    companion object {
        const val TAG = "ReportListFragment"
        fun newInstance(): Fragment = ReportListFragment()
    }

    override val vm: IReportListViewModel by sharedViewModel<ReportViewModel>()
    override val layoutId: Int = R.layout.fragment_report_list
    override val withOptionsMenu = true

    private lateinit var mRefresh: SwipeRefreshLayout
    private lateinit var mToolbar: Toolbar
    private lateinit var mAdd: FloatingActionButton
    private lateinit var mList: RecyclerView
    private lateinit var mAdapter: ReportListAdapter
    override fun setUpBindings(view: View) {
        mRefresh = view.findViewById(R.id.refresh)
        mToolbar = view.findViewById(R.id.toolbar)
        mAdd = view.findViewById(R.id.add)
        mList = view.findViewById(R.id.list)
        mAdapter = ReportListAdapter()
    }

    override fun initView(state: Bundle?) {
        mList.adapter = mAdapter
        ItemTouchHelper(CItemTouchHelperCallback(mAdapter, allowSwipe = true, allowDrag = false)).attachToRecyclerView(mList)
    }

    override fun setUpListeners() {
        mAdapter.setListener(this)
        mAdd.setOnClickListener { vm.newReport() }
        mRefresh.setOnRefreshListener { vm.reloadReports() }
        mList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    vm.loadNextReports(mAdapter.itemCount)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.report_list, menu)
        val search = menu.findItem(R.id.search).actionView as? SearchView
        search?.setOnQueryTextListener(SearchListener(vm::newReportFilter))
    }

    override fun setUpObservers(vm: IReportListViewModel) {
        vm.reportListCommands.observe(this, Observer {
            when (it) {
                is ReportListCommand.SetReports -> mAdapter.setReports(it.reports)
                is ReportListCommand.AddReport -> mAdapter.addReport(it.report)
                is ReportListCommand.AddReports -> mAdapter.addReports(it.reports)
                is ReportListCommand.UpdateReport -> mAdapter.updateReport(it.report)
//                is ReportListCommand.DeleteReport -> mAdapter.deleteReport(it.report)
            }
        })
    }

    override fun delete(id: UUID) = vm.deleteReport(id)

    override fun click(id: UUID) = vm.selectReport(id)

}