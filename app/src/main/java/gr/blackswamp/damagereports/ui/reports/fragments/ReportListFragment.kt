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
import gr.blackswamp.core.widget.onClick
import gr.blackswamp.core.widget.onNavigationClick
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
        add.onClick(this::newReport)
        refresh.setOnRefreshListener { vm.reloadReports() }
        (toolbar.menu?.findItem(R.id.search_reports)?.actionView as? SearchView)?.setOnQueryTextListener(SearchListener(this::newFilter))
        toolbar.menu?.findItem(R.id.switch_theme)?.onClick(this::toggleTheme)
        toolbar.onNavigationClick(this::backClicked)
    }

    override fun setUpObservers(vm: ReportListViewModel) {
        vm.reportHeaderList.observe(adapter::submitList)
        vm.refreshing.observe(this, Observer {
            if (it == false)
                refresh.isRefreshing = false
        })
        vm.darkTheme.observe(this::updateTheme)
    }

    //region observers
    private fun updateTheme(dark: Boolean?) {
        if (dark == true) {
            theme.setTitle(R.string.switch_to_light)
            theme.setIcon(R.drawable.ic_brightness_7_on_control)
        } else {
            theme.setTitle(R.string.switch_to_dark)
            theme.setIcon(R.drawable.ic_brightness_4_on_control)
        }
    }
    //endregion

    //region listeners
    private fun newReport() = vm.newReport()

    private fun newFilter(filter: String, submitted: Boolean): Boolean = vm.newReportFilter(filter, submitted)

    private fun toggleTheme() = vm.toggleTheme()

    private fun backClicked() {
        Toast.makeText(activity!!, "BACK", Toast.LENGTH_LONG).show()
    }

    //endregion

    override fun delete(id: UUID) = vm.deleteReport(id)

    override fun select(id: UUID) = vm.selectReport(id)

    override fun edit(id: UUID) = vm.editReport(id)
}