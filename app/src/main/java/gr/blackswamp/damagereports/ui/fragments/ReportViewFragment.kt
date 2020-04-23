package gr.blackswamp.damagereports.ui.fragments

import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.widget.TextChangeListener
import gr.blackswamp.core.widget.updateText
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.FragmentReportViewBinding
import gr.blackswamp.damagereports.logic.commands.ReportViewCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ReportViewViewModel
import gr.blackswamp.damagereports.logic.vms.MainViewModelImpl
import gr.blackswamp.damagereports.logic.vms.ReportViewViewModelImpl
import gr.blackswamp.damagereports.ui.model.Report
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Suppress("UNUSED_PARAMETER")
class ReportViewFragment : CoreFragment<ReportViewViewModel, FragmentReportViewBinding>() {
    companion object {
        const val TAG = "ReportViewFragment"
    }

    private val nameListener = TextChangeListener(after = this::nameChanged)
    private val descriptionListener = TextChangeListener(after = this::descriptionChanged)

    //region bindings
    private val parent: FragmentParent by sharedViewModel<MainViewModelImpl>()
    private val args: ReportViewFragmentArgs by navArgs()
    override val vm: ReportViewViewModel by viewModel<ReportViewViewModelImpl> { parametersOf(parent, args.report, args.inEditMode) }
    override val binding: FragmentReportViewBinding by lazy { FragmentReportViewBinding.inflate(layoutInflater) }
    override val optionsMenuId: Int = R.menu.report_view
    private val id: TextView by lazy { binding.id }
    private val name: EditText by lazy { binding.name }
    private val description: EditText by lazy { binding.description }
    private val model: Button by lazy { binding.model }
    private val brand: Button by lazy { binding.brand }
    private val refreshDamages: SwipeRefreshLayout by lazy { binding.refresh }
    private val damages: RecyclerView by lazy { binding.damages }
    private val action: FloatingActionButton by lazy { binding.action }
    //endregion

    //region lifecycle methods
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val editing = vm.editMode.value == true
        menu.findItem(R.id.edit)?.isVisible = !editing
        menu.findItem(R.id.save)?.isVisible = editing
    }

    override fun setUpListeners() {
        model.setOnClickListener(this::pickModel)
        brand.setOnClickListener(this::pickBrand)
        name.addTextChangedListener(nameListener)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
        description.addTextChangedListener(descriptionListener)
    }

    override fun setUpObservers(vm: ReportViewViewModel) {
        vm.report.observe(this::updateReport)
        vm.editMode.observe(this::updateEditable)
        vm.command.observe(this::executeCommand)
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
        backPressedCallback.isEnabled = vm.editMode.value == true
//        updateNavigationIcon(report, vm.editMode.value)
        requireActivity().invalidateOptionsMenu()

    }

    private fun updateEditable(editable: Boolean?) {
        setEditable(editable == true)
        backPressedCallback.isEnabled = editable == true
//        updateNavigationIcon(vm.report.value, editable)
        requireActivity().invalidateOptionsMenu()
    }

    private fun executeCommand(command: ReportViewCommand?) {
        when (command) {
            is ReportViewCommand.ShowBrandSelect -> {
                val action = ReportViewFragmentDirections.selectBrand()
                findNavController().navigate(action)
            }
            is ReportViewCommand.ShowModelSelect -> {
                val action = ReportViewFragmentDirections.selectModel(command.brand)
                findNavController().navigate(action)
            }
        }
    }

    //endregion
    //region listeners
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Toast.makeText(context, "exit pressed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                vm.saveReport()
                true
            }
            R.id.edit -> {
                vm.editReport()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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
//        save.isVisible = editable
//        edit.isVisible = !editable

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
//            toolbar.setNavigationIcon(R.drawable.ic_nav_back)
        } else {
//            toolbar.setNavigationIcon(R.drawable.ic_undo)
        }
    }
    //endregion
}