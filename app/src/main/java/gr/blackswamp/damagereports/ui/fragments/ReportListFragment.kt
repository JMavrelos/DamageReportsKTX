package gr.blackswamp.damagereports.ui.fragments


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import gr.blackswamp.core.widget.CItemTouchHelperCallback
import gr.blackswamp.core.widget.SearchListener
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.FragmentReportListBinding
import gr.blackswamp.damagereports.ui.activities.IViewModelActivity
import gr.blackswamp.damagereports.ui.adapters.OnListAction
import java.util.*


class ReportListFragment : Fragment(), OnListAction {
    companion object {
        const val TAG = "ReportListFragment"
        fun newInstance(): Fragment = ReportListFragment()
    }

    private lateinit var viewModel: ReportListViewModel
    lateinit var binding: FragmentReportListBinding
//    lateinit var adapter: ReportListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        adapter = ReportListAdapter(this)
//        binding.list.adapter = adapter
//        ItemTouchHelper(CItemTouchHelperCallback(adapter, true, false)).attachToRecyclerView(binding.list)
        binding.add.setOnClickListener(this::newReport)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = (activity as IViewModelActivity).viewModel as ReportListViewModel
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
//        viewModel.reports.observe(this,
//            Observer {
//                adapter.submitList(it)
////                adapter.setItems(it)
//                binding.refresh.isRefreshing = false
//            }
//        )
//        binding.refresh.setOnRefreshListener { viewModel.refresh() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.report_list, menu)
        val search = menu.findItem(R.id.search).actionView as SearchView
        search.setOnQueryTextListener(SearchListener(this::filterChanged))
    }

    fun filterChanged(text: String, submitted: Boolean): Boolean {
//        if (submitted) {
//            viewModel.newReportListFilter(text)
            return true
//        }
//        return false
    }

    @Suppress("UNUSED_PARAMETER")
    private fun newReport(view: View) {
//        viewModel.newReport()
    }

    override fun delete(id: UUID) {
//        viewModel.deleteReport(id)
    }

    override fun click(id: UUID) {
//        viewModel.selectReport(id)
    }

    interface ReportListViewModel {
        //        val reports: Observable<PagedList<ReportHeader>>
//        val reports: LiveData<PagedList<ReportHeader>>
//
//        fun newReport()
//        fun refresh()
//        fun deleteReport(id: UUID)
//        fun selectReport(id: UUID)
//        fun newReportListFilter(text: String)
    }

}