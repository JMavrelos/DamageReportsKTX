package gr.blackswamp.damagereports.ui.reports.fragments

import android.os.Bundle
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
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import gr.blackswamp.damagereports.vms.reports.viewmodels.IReportViewViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ReportViewFragment : CoreFragment<IReportViewViewModel>() {
    companion object {
        const val TAG = "ReportViewFragment"
        fun newInstance(): Fragment = ReportViewFragment()
    }

    override val vm: IReportViewViewModel by sharedViewModel<ReportViewModel>()
    override val layoutId: Int = R.layout.fragment_report_view


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

    override fun initView(state: Bundle?) {

    }

    override fun setUpListeners() {
        model.setOnClickListener { vm.pickModel() }
        brand.setOnClickListener { vm.pickBrand() }
        save.setOnMenuItemClickListener { vm.saveReport(); true }
        edit.setOnMenuItemClickListener { vm.editReport(); true }
        toolbar.setNavigationOnClickListener { vm.exitReport() }
    }

    override fun setUpObservers(vm: IReportViewViewModel) {
        vm.report.observe(this, Observer { showReport(it) })
        vm.editMode.observe(this, Observer { setEditable(it == true) })

    }

    private fun setEditable(editable: Boolean) {
        save.isVisible = editable
        edit.isVisible = !editable
        model.isEnabled = editable
        brand.isEnabled = editable
        description.isEnabled = editable
        name.isEnabled = editable
        if (editable) {
            action.show()
            toolbar.setNavigationIcon(R.drawable.ic_undo)
        } else {
            action.hide()
            toolbar.setNavigationIcon(R.drawable.ic_nav_back)
            name.clearFocus()
            description.clearFocus()
        }
    }

    private fun showReport(report: Report?) {
        if (report == null) return
        id.text = report.id.toString()
        name.setText(report.name)
        description.setText(report.description)
        model.text = report.modelName ?: getString(R.string.select_model)
        brand.text = report.brandName ?: getString(R.string.select_brand)
    }
}