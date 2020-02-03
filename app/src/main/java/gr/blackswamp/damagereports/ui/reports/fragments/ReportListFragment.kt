package gr.blackswamp.damagereports.ui.reports.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.core.widget.CItemTouchHelperCallback
import gr.blackswamp.core.widget.SearchListener
import gr.blackswamp.core.widget.onClick
import gr.blackswamp.core.widget.onNavigationClick
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.ui.reports.adapters.ReportListAction
import gr.blackswamp.damagereports.ui.reports.adapters.ReportListAdapter
import gr.blackswamp.damagereports.ui.reports.commands.ReportListCommand
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
    private lateinit var settings: MenuItem
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var darkTheme: View
    private lateinit var lightTheme: View
    private lateinit var systemTheme: View
    private lateinit var autoTheme: View
    private lateinit var closeTheme: View

    override fun setUpBindings(view: View) {
        refresh = view.findViewById(R.id.refresh)
        add = view.findViewById(R.id.add)
        list = view.findViewById(R.id.list)
        adapter = ReportListAdapter()
        toolbar = view.findViewById(R.id.toolbar)
        settings = toolbar.menu.findItem(R.id.settings)
        val themeSelection = view.findViewById<LinearLayout>(R.id.theme_selection)
        sheetBehavior = BottomSheetBehavior.from(themeSelection)
        darkTheme = view.findViewById(R.id.dark)
        lightTheme = view.findViewById(R.id.light)
        systemTheme = view.findViewById(R.id.system)
        autoTheme = view.findViewById(R.id.auto)
        closeTheme = view.findViewById(R.id.cancel_theme)
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
        toolbar.menu?.findItem(R.id.settings)?.onClick(vm::showThemeSettings)
        toolbar.onNavigationClick(this::backClicked)
        darkTheme.onClick { vm.changeTheme(ThemeSetting.Dark) }
        lightTheme.onClick { vm.changeTheme(ThemeSetting.Light) }
        systemTheme.onClick { vm.changeTheme(ThemeSetting.System) }
        autoTheme.onClick { vm.changeTheme(ThemeSetting.Auto) }
        closeTheme.onClick { updateBottomSheet(null) }
    }

    override fun setUpObservers(vm: ReportListViewModel) {
        vm.reportHeaderList.observe(adapter::submitList)
        vm.refreshing.observe {
            if (it == false)
                refresh.isRefreshing = false
        }
        vm.listCommand.observe(this::executeCommand)
    }
    //endregion

    //region listeners
    private fun newReport() = vm.newReport()

    private fun newFilter(filter: String, submitted: Boolean): Boolean = vm.newReportFilter(filter, submitted)

    private fun backClicked() {
        Toast.makeText(activity!!, "BACK", Toast.LENGTH_LONG).show()
    }

    private fun executeCommand(cmd: ReportListCommand?) {
        if (cmd is ReportListCommand.ShowThemeSelection) {
            updateBottomSheet(cmd.current)
        }
    }
    //endregion

    //region commands
    private fun updateBottomSheet(setting: ThemeSetting?) {
        if (setting != null) {
            sheetBehavior.state = STATE_EXPANDED
            darkTheme.isActivated = setting == ThemeSetting.Dark
            lightTheme.isActivated = setting == ThemeSetting.Light
            systemTheme.isActivated = setting == ThemeSetting.System
            autoTheme.isActivated = setting == ThemeSetting.Auto
        } else {
            sheetBehavior.state = STATE_COLLAPSED
        }
    }
    //endregion

    override fun delete(id: UUID) = vm.deleteReport(id)

    override fun select(id: UUID) = vm.selectReport(id)

    override fun edit(id: UUID) = vm.editReport(id)
}