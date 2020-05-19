package gr.blackswamp.damagereports.ui.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import gr.blackswamp.core.widget.visible
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.FragmentModelBinding
import gr.blackswamp.damagereports.logic.commands.ModelCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ModelViewModel
import gr.blackswamp.damagereports.logic.vms.MainViewModelImpl
import gr.blackswamp.damagereports.logic.vms.ModelViewModelImpl
import gr.blackswamp.damagereports.ui.adapters.ListAction
import gr.blackswamp.damagereports.ui.adapters.ModelAdapter
import gr.blackswamp.damagereports.ui.model.Model
import gr.blackswamp.damagereports.utils.moveBy
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*
import kotlin.math.abs

class ModelFragment : CoreFragment<ModelViewModel, FragmentModelBinding>(), ListAction {
    companion object {
        const val TAG = "ModelFragment"
        fun newInstance(): Fragment = ModelFragment()
    }

    //region  bindings
    private val parent: FragmentParent by sharedViewModel<MainViewModelImpl>()
    private val args: ModelFragmentArgs by navArgs()
    override val vm: ModelViewModel by viewModel<ModelViewModelImpl> { parametersOf(parent, args.brand.id) }
    override val binding: FragmentModelBinding by lazy { FragmentModelBinding.inflate(layoutInflater) }
    override val optionsMenuId: Int = R.menu.list
    private val refresh: SwipeRefreshLayout by lazy { binding.refresh }
    private val action: FloatingActionButton by lazy { binding.action }
    private val cancel: FloatingActionButton by lazy { binding.cancel }
    private val list: RecyclerView by lazy { binding.list }
    private val name: EditText by lazy { binding.name }
    private val adapter = ModelAdapter()
    private val sheetBehavior: BottomSheetBehavior<FrameLayout> by lazy { BottomSheetBehavior.from(binding.create) }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        (menu.findItem(R.id.list_search)?.actionView as? SearchView)?.setOnQueryTextListener(SearchListener(vm::newFilter))
    }

    override fun setUpListeners() {
        adapter.setListener(this)
        action.onClick(this::actionClick)
        refresh.onClick(this::refresh)
        cancel.onClick(vm::cancel)
        sheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun setUpObservers(vm: ModelViewModel) {
        vm.model.observe(this::showModel)
        vm.command.observe(this::executeCommand)
    }

    //endregion

    //region listeners
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_use -> {
                toggleByUse()
                true
            }
            R.id.sort_name -> {
                toggleByName()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun delete(id: UUID) = vm.delete(id)

    override fun select(id: UUID) = vm.select(id)

    override fun edit(id: UUID) = vm.edit(id)

    private fun toggleByUse() {

    }

    private fun toggleByName() {

    }

    private fun actionClick() {
        if (sheetBehavior.state == STATE_EXPANDED) {
            vm.save(name.text.toString())
        } else {
            vm.create()
        }
    }

    private fun refresh() = vm.refresh()

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

    private fun executeCommand(command: ModelCommand?) {
        when (command) {
            is ModelCommand.ModelSelected -> {
                val selected = command.model
                val action = ModelFragmentDirections.finishModel()
                action.arguments.putParcelable("RESULT", selected)
                findNavController().navigate(action)
            }
        }
    }
    //endregion
}