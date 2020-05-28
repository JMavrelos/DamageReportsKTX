package gr.blackswamp.damagereports.ui.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.core.widget.CItemTouchHelperCallback
import gr.blackswamp.core.widget.SearchListener
import gr.blackswamp.core.widget.onClick
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.databinding.FragmentReportListBinding
import gr.blackswamp.damagereports.logic.commands.ReportListCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ReportListViewModel
import gr.blackswamp.damagereports.logic.vms.MainViewModelImpl
import gr.blackswamp.damagereports.logic.vms.ReportListViewModelImpl
import gr.blackswamp.damagereports.ui.adapters.ListAction
import gr.blackswamp.damagereports.ui.adapters.ReportListAdapter
import gr.blackswamp.damagereports.utils.moveBy
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*

class ReportListFragment : CoreFragment<ReportListViewModel, FragmentReportListBinding>(), ListAction {
    companion object {
        const val TAG = "ReportListFragment"
    }

    //region bindings
    private val parent: FragmentParent by sharedViewModel<MainViewModelImpl>()
    override val vm: ReportListViewModel by viewModel<ReportListViewModelImpl> { parametersOf(parent) }
    override val binding: FragmentReportListBinding by lazy { FragmentReportListBinding.inflate(layoutInflater) }
    override val optionsMenuId: Int = R.menu.report_list
    private val refresh: SwipeRefreshLayout by lazy { binding.refresh }
    private val action: FloatingActionButton by lazy { binding.action }
    private val list: RecyclerView by lazy { binding.list }
    private val adapter: ReportListAdapter by lazy { ReportListAdapter() }
    private val sheetBehavior: BottomSheetBehavior<LinearLayout> by lazy { BottomSheetBehavior.from(binding.themeSelection) }
    private val darkTheme: View by lazy { binding.dark }
    private val lightTheme: View by lazy { binding.light }
    private val systemTheme: View by lazy { binding.system }
    private val autoTheme: View by lazy { binding.auto }
    private var screenWidth: Int = 0
    //endregion

    override fun initView(state: Bundle?) {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        screenWidth = metrics.widthPixels
        list.adapter = adapter
        ItemTouchHelper(CItemTouchHelperCallback(adapter, allowSwipe = true, allowDrag = false)).attachToRecyclerView(list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        (menu.findItem(R.id.search_reports)?.actionView as? SearchView)?.setOnQueryTextListener(SearchListener(this::newFilter))
    }

    override fun setUpListeners() {
        adapter.setListener(this)
        action.onClick(this::actionClick)
        refresh.setOnRefreshListener { vm.reloadReports() }
        darkTheme.onClick { vm.changeTheme(ThemeSetting.Dark) }
        lightTheme.onClick { vm.changeTheme(ThemeSetting.Light) }
        systemTheme.onClick { vm.changeTheme(ThemeSetting.System) }
        autoTheme.onClick { vm.changeTheme(ThemeSetting.Auto) }
        sheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun setUpObservers(vm: ReportListViewModel) {
        vm.reportHeaderList.observe(adapter::submitList)
        vm.refreshing.observe { refresh.isRefreshing = it == true }
        vm.command.observe(this::executeCommand)
        vm.themeSelection.observe(this::updateBottomSheet)
    }

    //endregion

    //region listeners
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.theme -> {
                vm.showThemeSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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

    //endregion

    //region commands
    private fun executeCommand(command: ReportListCommand?) {
        when (command) {
            is ReportListCommand.ShowReport -> {
                val action = ReportListFragmentDirections.showReport(command.report, command.inEditMode)
                findNavController().navigate(action)
            }
        }
    }

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