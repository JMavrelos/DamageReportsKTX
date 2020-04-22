package gr.blackswamp.damagereports.ui.fragments

import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.widget.TextChangeListener
import gr.blackswamp.core.widget.updateText
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.FragmentReportViewBinding
import gr.blackswamp.damagereports.logic.vms.ReportViewViewModel
import gr.blackswamp.damagereports.logic.vms.ReportViewViewModelImpl
import gr.blackswamp.damagereports.ui.model.Report
import org.koin.android.viewmodel.ext.android.viewModel

@Suppress("UNUSED_PARAMETER")
class ReportViewFragment : CoreFragment<ReportViewViewModel, FragmentReportViewBinding>() {
    companion object {
        const val TAG = "ReportViewFragment"
        fun newInstance(): Fragment = ReportViewFragment()
    }

    private val nameListener = TextChangeListener(after = this::nameChanged)
    private val descriptionListener = TextChangeListener(after = this::descriptionChanged)

    //region bindings
    override val vm: ReportViewViewModel by viewModel<ReportViewViewModelImpl>()
    override val binding: FragmentReportViewBinding by lazy { FragmentReportViewBinding.inflate(layoutInflater) }
    private val toolbar: Toolbar by lazy { binding.toolbar }
    private val id: TextView by lazy { binding.id }
    private val name: EditText by lazy { binding.name }
    private val description: EditText by lazy { binding.description }
    private val model: Button by lazy { binding.model }
    private val brand: Button by lazy { binding.brand }
    private val refreshDamages: SwipeRefreshLayout by lazy { binding.refresh }
    private val damages: RecyclerView by lazy { binding.damages }
    private val action: FloatingActionButton by lazy { binding.action }
    private val save: MenuItem by lazy { binding.toolbar.menu.findItem(R.id.save) }
    private val edit: MenuItem by lazy { binding.toolbar.menu.findItem(R.id.edit) }
    //endregion

    //region lifecycle methods
    override fun setUpListeners() {
        model.setOnClickListener(this::pickModel)
        brand.setOnClickListener(this::pickBrand)
        save.setOnMenuItemClickListener(this::saveReport)
        edit.setOnMenuItemClickListener(this::editReport)
        toolbar.setNavigationOnClickListener(this::exit)
        name.addTextChangedListener(nameListener)
        description.addTextChangedListener(descriptionListener)
    }

    override fun setUpObservers(vm: ReportViewViewModel) {
        vm.report.observe(this::updateReport)
        vm.editMode.observe(this::updateEditable)
    }

    override fun onDestroy() {
        super.onDestroy()
        name.removeTextChangedListener(nameListener)
        description.removeTextChangedListener(descriptionListener)
    }
    //endregion

    //region observers
    private fun updateReport(report: Report?) {
        showReport(report)
        updateNavigationIcon(report, vm.editMode.value)

    }

    private fun updateEditable(editable: Boolean?) {
        setEditable(editable == true)
        updateNavigationIcon(vm.report.value, editable)
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