package gr.blackswamp.damagereports.ui.make.fragments

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.marginEnd
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
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.widget.*
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.base.ListAction
import gr.blackswamp.damagereports.ui.make.adapters.ModelAdapter
import gr.blackswamp.damagereports.ui.model.Model
import gr.blackswamp.damagereports.vms.ModelData
import gr.blackswamp.damagereports.vms.make.MakeViewModelImpl
import gr.blackswamp.damagereports.vms.make.viewmodels.ModelParent
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.math.abs

class ModelFragment : CoreFragment<ModelParent>(), ListAction {
    companion object {
        const val TAG = "ModelFragment"
        fun newInstance(): Fragment = ModelFragment()
    }

    override val vm: ModelParent by sharedViewModel<MakeViewModelImpl>()
    override val layoutId: Int = R.layout.fragment_make

    //region view bindings
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var action: FloatingActionButton
    private lateinit var cancel: FloatingActionButton
    private lateinit var list: RecyclerView
    private lateinit var name: EditText
    private var adapter = ModelAdapter()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var sheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var byUse: MenuItem
    private lateinit var byName: MenuItem
    //endregion

    //region set up
    override fun setUpBindings(view: View) {
        refresh = view.findViewById(R.id.refresh)
        action = view.findViewById(R.id.action)
        cancel = view.findViewById(R.id.cancel)
        list = view.findViewById(R.id.list)
        toolbar = view.findViewById(R.id.toolbar)
        name = view.findViewById(R.id.name)
        byName = toolbar.menu.findItem(R.id.sort_name)
        byUse = toolbar.menu.findItem(R.id.sort_use)
        val brandInput = view.findViewById<FrameLayout>(R.id.create)
        sheetBehavior = BottomSheetBehavior.from(brandInput)
    }

    override fun initView(state: Bundle?) {
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

    override fun setUpObservers(vm: ModelParent) {

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
        showModel(ModelData(UUID.randomUUID(), "test", EmptyUUID))
    }

    private fun toggleByName() {

    }

    private fun actionClick() {
        if (sheetBehavior.state == STATE_EXPANDED) {
            Toast.makeText(activity!!, "Save", Toast.LENGTH_LONG).show()
            showModel(null)
        } else {
            showModel(ModelData(UUID.randomUUID(), "", EmptyUUID))
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
        showModel(null)
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (isAdded) {
                //this where the cancel button should move to
                val offset = ((this@ModelFragment.view?.measuredWidth ?: 0) - cancel.width - (cancel.paddingEnd * 2) - (cancel.marginEnd * 2)) / 2
                cancel.rotation = (-slideOffset * 360f)
                cancel.alpha = abs(slideOffset)
                cancel.visible = slideOffset > 0f
                cancel.translationX = (slideOffset * (-offset))

                action.rotation = (slideOffset * 360f)
                if (slideOffset == 1f) {
                    action.setImageResource(R.drawable.ic_save)
                } else if (slideOffset <= 0f) {
                    action.setImageResource(R.drawable.ic_add)
                }
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {}

    }
    //region listeners

    //region private functions
    private fun showModel(model: Model?) {
        if (model == null) {
            sheetBehavior.state = STATE_COLLAPSED
        } else {
            name.setText(model.name)
            name.selectAll()
            sheetBehavior.state = STATE_EXPANDED
        }
    }

    //endregion

}