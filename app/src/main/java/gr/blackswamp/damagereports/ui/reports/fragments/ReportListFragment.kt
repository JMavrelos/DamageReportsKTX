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
import gr.blackswamp.damagereports.vms.reports.viewmodels.ReportListViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class ReportListFragment : CoreFragment<ReportListViewModel>(), ReportListAction {
    companion object {
        const val TAG = "ReportListFragment"
        fun newInstance(): Fragment = ReportListFragment()
    }


    override val vm: ReportListViewModel by sharedViewModel<ReportViewModel>()
    override val layoutId: Int = R.layout.fragment_report_list

    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var add: FloatingActionButton
    private lateinit var list: RecyclerView
    private lateinit var adapter: ReportListAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var theme: MenuItem

    override fun setUpBindings(view: View) {
        refresh = view.findViewById(R.id.refresh)
        add = view.findViewById(R.id.add)
        list = view.findViewById(R.id.list)
        adapter = ReportListAdapter()
        toolbar = view.findViewById(R.id.toolbar)
        theme = toolbar.menu.findItem(R.id.switch_theme)
    }

    override fun initView(state: Bundle?) {
        list.adapter = adapter
        ItemTouchHelper(CItemTouchHelperCallback(adapter, allowSwipe = true, allowDrag = false)).attachToRecyclerView(list)
    }

    override fun setUpListeners() {
        adapter.setListener(this)
        add.setOnClickListener { vm.newReport() }
        refresh.setOnRefreshListener { vm.reloadReports() }
        (toolbar.menu?.findItem(R.id.search_reports)?.actionView as? SearchView)?.setOnQueryTextListener(SearchListener(vm::newReportFilter))
        toolbar.menu?.findItem(R.id.switch_theme)?.setOnMenuItemClickListener { vm.toggleTheme();true }
        toolbar.setNavigationOnClickListener { Toast.makeText(activity!!, "BACK", Toast.LENGTH_LONG).show() }
    }

    override fun setUpObservers(vm: ReportListViewModel) {
        vm.reportHeaderList.observe(this, Observer { adapter.submitList(it) })
        vm.refreshing.observe(this, Observer {
            if (it == false)
                refresh.isRefreshing = false
        })
        vm.darkTheme.observe(this, Observer {
            if (it == true) {
                theme.setTitle(R.string.switch_to_light)
                theme.setIcon(R.drawable.ic_brightness_7_on_control)
            } else {
                theme.setTitle(R.string.switch_to_dark)
                theme.setIcon(R.drawable.ic_brightness_4_on_control)
            }
        })
    }

    override fun delete(id: UUID) = vm.deleteReport(id)

    override fun select(id: UUID) = vm.selectReport(id)

    override fun edit(id: UUID) = vm.editReport(id)
}