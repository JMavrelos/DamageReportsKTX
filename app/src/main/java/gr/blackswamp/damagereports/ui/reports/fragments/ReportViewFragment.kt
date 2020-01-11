package gr.blackswamp.damagereports.ui.reports.fragments

import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.widget.TextChangeListener
import gr.blackswamp.core.widget.updateText
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.base.BaseFragment
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportViewViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

@Suppress("UNUSED_PARAMETER")
class ReportViewFragment : BaseFragment<IReportViewViewModel>() {
    companion object {
        const val TAG = "ReportViewFragment"
        fun newInstance(): Fragment = ReportViewFragment()
    }

    override val vm: IReportViewViewModel by sharedViewModel<ReportViewModel>()
    override val layoutId: Int = R.layout.fragment_report_view
    private val nameListener = TextChangeListener(after = this::nameChanged)
    private val descriptionListener = TextChangeListener(after = this::descriptionChanged)

    //region bindings
    private lateinit var toolbar: Toolbar
    private lateinit var id: TextView
    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var model: Button
    private lateinit var brand: Button
    private lateinit var refreshDamages: SwipeRefreshLayout
    private lateinit var damages: RecyclerView
    private lateinit var action: FloatingActionButton
    private lateinit var save: MenuItem
    private lateinit var edit: MenuItem
    //endregion

    //region lifecycle methods
    override fun setUpBindings(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        save = toolbar.menu.findItem(R.id.save)
        edit = toolbar.menu.findItem(R.id.edit)
        id = view.findViewById(R.id.id)
        name = view.findViewById(R.id.name)
        description = view.findViewById(R.id.description)
        model = view.findViewById(R.id.model)
        brand = view.findViewById(R.id.brand)
        refreshDamages = view.findViewById(R.id.refresh)
        damages = view.findViewById(R.id.damages)
        action = view.findViewById(R.id.action)
    }

    override fun setUpListeners() {
        model.setOnClickListener(this::pickModel)
        brand.setOnClickListener(this::pickBrand)
        save.setOnMenuItemClickListener(this::saveReport)
        edit.setOnMenuItemClickListener(this::editReport)
        toolbar.setNavigationOnClickListener(this::exit)
        name.addTextChangedListener(nameListener)
        description.addTextChangedListener(descriptionListener)
    }

    override fun setUpObservers(vm: IReportViewViewModel) {
        vm.report.observe(this, Observer {
            showReport(it)
            updateNavigationIcon(it, vm.editMode.value)
        })
        vm.editMode.observe(this, Observer {
            setEditable(it == true)
            updateNavigationIcon(vm.report.value, it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        name.removeTextChangedListener(nameListener)
        description.removeTextChangedListener(descriptionListener)
    }
    //endregion

    //region listeners
    private fun pickModel(v: View) = vm.pickModel()

    private fun pickBrand(v: View) = vm.pickBrand()

    private fun descriptionChanged(editable: Editable?) {
        vm.descriptionChanged(editable?.toString() ?: "")
    }

    private fun nameChanged(editable: Editable?) {
        vm.nameChanged(editable?.toString() ?: "")
    }

    private fun saveReport(item: MenuItem): Boolean {
        vm.saveReport()
        return true
    }

    private fun editReport(item: MenuItem): Boolean {
        vm.editReport()
        return true
    }

    private fun exit(v: View) = vm.exitReport()

    //endregion

    //region private methods
    private fun setEditable(editable: Boolean) {
        save.isVisible = editable
        edit.isVisible = !editable
        model.isEnabled = editable
        brand.isEnabled = editable
        description.isEnabled = editable
        name.isEnabled = editable
        if (editable) {
            action.show()
        } else {
            action.hide()
            name.clearFocus()
            description.clearFocus()
        }
    }

    private fun showReport(report: Report?) {
        if (report == null) return
        try {
            nameListener.pause()
            descriptionListener.pause()
            id.text = report.id.toString()
            name.updateText(report.name)
            description.updateText(report.description)
            model.text = report.modelName ?: getString(R.string.select_model)
            brand.text = report.brandName ?: getString(R.string.select_brand)
        } finally {
            nameListener.resume()
            descriptionListener.resume()
        }
    }

    private fun updateNavigationIcon(report: Report?, editMode: Boolean?) {
        val changed = report?.changed == true
        val new = report?.id == EmptyUUID
        val edit = editMode == true
        if (new || (edit && !changed)) {
            toolbar.setNavigationIcon(R.drawable.ic_nav_back)
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_undo)
        }
    }
    //endregion
}