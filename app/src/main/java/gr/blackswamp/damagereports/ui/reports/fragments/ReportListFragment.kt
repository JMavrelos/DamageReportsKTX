package gr.blackswamp.damagereports.ui.reports.fragments

import android.os.Bundle
import android.util.DisplayMetrics
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
import gr.blackswamp.damagereports.ui.base.ListAction
import gr.blackswamp.damagereports.ui.moveBy
import gr.blackswamp.damagereports.ui.reports.adapters.ReportListAdapter
import gr.blackswamp.damagereports.vms.reports.ReportViewModelImpl
import gr.blackswamp.damagereports.vms.reports.viewmodels.ReportListParent
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*

class ReportListFragment : CoreFragment<ReportListParent>(), ListAction {
    companion object {
        const val TAG = "ReportListFragment"
        fun newInstance(): Fragment = ReportListFragment()
    }

    override val vm: ReportListParent by sharedViewModel<ReportViewModelImpl>()
    override val layoutId: Int = R.layout.fragment_report_list

    //region view bindings
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var action: FloatingActionButton
    private lateinit var list: RecyclerView
    private val adapter = ReportListAdapter()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var theme: MenuItem
    private lateinit var themeSelection: LinearLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var darkTheme: View
    private lateinit var lightTheme: View
    private lateinit var systemTheme: View
    private lateinit var autoTheme: View
    private var screenWidth: Int = 0
    //endregion

    //region set up
    override fun setUpBindings(view: View) {
        refresh = view.findViewById(R.id.refresh)
        action = view.findViewById(R.id.action)
        list = view.findViewById(R.id.list)
        toolbar = view.findViewById(R.id.toolbar)
        theme = toolbar.menu.findItem(R.id.theme)
        themeSelection = view.findViewById(R.id.theme_selection)
        sheetBehavior = BottomSheetBehavior.from(themeSelection)
        darkTheme = view.findViewById(R.id.dark)
        lightTheme = view.findViewById(R.id.light)
        systemTheme = view.findViewById(R.id.system)
        autoTheme = view.findViewById(R.id.auto)
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        screenWidth = metrics.widthPixels
    }

    override fun initView(state: Bundle?) {
        list.adapter = adapter
        ItemTouchHelper(CItemTouchHelperCallback(adapter, allowSwipe = true, allowDrag = false)).attachToRecyclerView(list)
    }

    override fun setUpListeners() {
        adapter.setListener(this)
        action.onClick(this::actionClick)
        refresh.setOnRefreshListener { vm.reloadReports() }
        (toolbar.menu?.findItem(R.id.search_reports)?.actionView as? SearchView)?.setOnQueryTextListener(SearchListener(this::newFilter))
        toolbar.menu?.findItem(R.id.theme)?.onClick(vm::showThemeSettings)
        toolbar.onNavigationClick(this::backClicked)
        darkTheme.onClick { vm.changeTheme(ThemeSetting.Dark) }
        lightTheme.onClick { vm.changeTheme(ThemeSetting.Light) }
        systemTheme.onClick { vm.changeTheme(ThemeSetting.System) }
        autoTheme.onClick { vm.changeTheme(ThemeSetting.Auto) }
        sheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun setUpObservers(vm: ReportListParent) {
        vm.reportHeaderList.observe(adapter::submitList)
        vm.refreshing.observe {
            if (it == false)
                refresh.isRefreshing = false
        }
        vm.themeSelection.observe(this::updateBottomSheet)
    }

    //endregion

    //region listeners

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            Timber.d("Sliding $slideOffset")
            if (isAdded && slideOffset > 0f && slideOffset < 1f) {
                updateActionButton(slideOffset)
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
    }

    private fun actionClick() {
        if (sheetBehavior.state == STATE_COLLAPSED) {
            vm.newReport()
        } else {
            vm.closeThemeSelection()
        }
    }

    private fun newFilter(filter: String, submitted: Boolean): Boolean = vm.newReportFilter(filter, submitted)

    private fun backClicked() {
        Toast.makeText(activity!!, "BACK", Toast.LENGTH_LONG).show()
    }
    //endregion

    //region commands
    private fun updateBottomSheet(setting: ThemeSetting?) {
        if (setting != null) {
            updateActionButton(1f)
            sheetBehavior.saveFlags
            sheetBehavior.state = STATE_EXPANDED
            darkTheme.isActivated = setting == ThemeSetting.Dark
            lightTheme.isActivated = setting == ThemeSetting.Light
            systemTheme.isActivated = setting == ThemeSetting.System
            autoTheme.isActivated = setting == ThemeSetting.Auto
        } else {
            updateActionButton(0f)
            sheetBehavior.state = STATE_COLLAPSED
        }
    }

    private fun updateActionButton(slideOffset: Float) {
        action.rotation = (-slideOffset * 135f)
        action.moveBy(slideOffset, screenWidth / 2)
    }
    //endregion

    override fun delete(id: UUID) = vm.deleteReport(id)

    override fun select(id: UUID) = vm.selectReport(id)

    override fun edit(id: UUID) = vm.editReport(id)
}