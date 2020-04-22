package gr.blackswamp.damagereports.ui.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
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
import gr.blackswamp.core.widget.*
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.FragmentModelBinding
import gr.blackswamp.damagereports.logic.vms.ModelViewModel
import gr.blackswamp.damagereports.logic.vms.ModelViewModelImpl
import gr.blackswamp.damagereports.ui.adapters.ListAction
import gr.blackswamp.damagereports.ui.adapters.ModelAdapter
import gr.blackswamp.damagereports.ui.model.Model
import gr.blackswamp.damagereports.utils.moveBy
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.abs

class ModelFragment : CoreFragment<ModelViewModel, FragmentModelBinding>(), ListAction {
    companion object {
        const val TAG = "ModelFragment"
        fun newInstance(): Fragment = ModelFragment()
    }

    //region  bindings
    override val vm: ModelViewModel by viewModel<ModelViewModelImpl>()
    override val binding: FragmentModelBinding by lazy { FragmentModelBinding.inflate(layoutInflater) }
    private val refresh: SwipeRefreshLayout by lazy { binding.refresh }
    private val action: FloatingActionButton by lazy { binding.action }
    private val cancel: FloatingActionButton by lazy { binding.cancel }
    private val list: RecyclerView by lazy { binding.list }
    private val name: EditText by lazy { binding.name }
    private val adapter = ModelAdapter()
    private val toolbar: MaterialToolbar by lazy { binding.toolbar }
    private val sheetBehavior: BottomSheetBehavior<FrameLayout> by lazy { BottomSheetBehavior.from(binding.create) }
    private val byUse: MenuItem by lazy { binding.toolbar.menu.findItem(R.id.sort_use) }
    private val byName: MenuItem by lazy { binding.toolbar.menu.findItem(R.id.sort_name) }
    private var screenWidth: Int = 0
    //endregion

    //region set up
    override fun initView(state: Bundle?) {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        screenWidth = metrics.widthPixels
        list.adapter = adapter
        ItemTouchHelper(CItemTouchHelperCallback(adapter, allowSwipe = true, allowDrag = false)).attachToRecyclerView(list)
    }

    override fun setUpListeners() {
        adapter.setListener(this)
        action.onClick(this::actionClick)
        refresh.onClick(this::refresh)
        byUse.onClick(this::toggleByUse)
        byName.onClick(this::toggleByName)
        (toolbar.menu?.findItem(R.id.list_search)?.actionView as? SearchView)?.setOnQueryTextListener(SearchListener(this::newFilter))
        toolbar.onNavigationClick(this::backClicked)
        cancel.onClick(this::cancelEdit)
        sheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun setUpObservers(vm: ModelViewModel) {
        vm.model.observe(this::showModel)
    }

    //endregion

    //region listeners
    override fun delete(id: UUID) {

    }

    override fun select(id: UUID) {

    }

    override fun edit(id: UUID) {

    }

    private fun toggleByUse() {
        vm.editModel(UUID.randomUUID())
    }

    private fun toggleByName() {

    }

    private fun actionClick() {
        if (sheetBehavior.state == STATE_EXPANDED) {
            vm.saveModel(name.text.toString())
        } else {
            vm.newModel()
        }
    }

    private fun refresh() {

    }

    private fun newFilter(filter: String, submitted: Boolean): Boolean {
        return true
    }

    private fun backClicked() {

    }

    private fun cancelEdit() {
        vm.cancelModel()
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (isAdded && slideOffset > 0f && slideOffset < 1f) {
                updateCancelButton(slideOffset)
                updateActionButton(slideOffset)
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) = Unit

    }
    //region listeners

    //region private functions
    private fun updateCancelButton(slideOffset: Float) {
        cancel.rotation = (-slideOffset * 360f)
        cancel.alpha = abs(slideOffset)
        cancel.visible = slideOffset > 0f
        cancel.moveBy(slideOffset, screenWidth / 2)
    }

    private fun updateActionButton(slideOffset: Float) {
        action.rotation = (slideOffset * 360f)
        if (slideOffset == 1f) {
            action.setImageResource(R.drawable.ic_save)
        } else if (slideOffset <= 0f) {
            action.setImageResource(R.drawable.ic_add)
        }
    }

    private fun showModel(model: Model?) {
        if (model == null) {
            updateActionButton(0f)
            updateCancelButton(0f)
            sheetBehavior.state = STATE_COLLAPSED
        } else {
            updateActionButton(1f)
            updateCancelButton(1f)
            name.setText(model.name)
            name.selectAll()
            sheetBehavior.state = STATE_EXPANDED
        }
    }
    //endregion
}