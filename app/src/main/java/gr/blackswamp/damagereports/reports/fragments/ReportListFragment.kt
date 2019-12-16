package gr.blackswamp.damagereports.reports.fragments


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.core.widget.SearchListener
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.reports.ReportViewModel
import gr.blackswamp.damagereports.reports.adapters.OnListAction
import gr.blackswamp.damagereports.reports.adapters.ReportListAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*


class ReportListFragment : CoreFragment<ReportViewModel>() {
    companion object {
        const val TAG = "ReportListFragment"
        fun newInstance(): Fragment = ReportListFragment()
    }


    override val vm: ReportViewModel by sharedViewModel()
    override val layoutId: Int = R.layout.fragment_report_list
    override val withOptionsMenu = true

    private lateinit var mRefresh: SwipeRefreshLayout
    private lateinit var mToolbar: Toolbar
    private lateinit var mAdd: FloatingActionButton
    private lateinit var mList: RecyclerView
    private lateinit var mAdapter: ReportListAdapter


    override fun setUpBindings(view: View) {
        mRefresh = view.findViewById(R.id.refresh)
        mToolbar = view.findViewById(R.id.toolbar)
        mAdd = view.findViewById(R.id.add)
        mList = view.findViewById(R.id.list)
        mAdapter = ReportListAdapter()
    }

    override fun setUpView(state: Bundle?) {
        (this.activity as? AppCompatActivity)?.setSupportActionBar(mToolbar)
    }

    override fun setUpListeners() {
        mAdapter.setListener(object : OnListAction {
            override fun delete(id: UUID) = vm.deleteReport(id)
            override fun click(id: UUID) = vm.selectReport(id)
        })
        mAdd.setOnClickListener { vm.newReport() }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.report_list, menu)
        val search = menu.findItem(R.id.search).actionView as? SearchView
        search?.setOnQueryTextListener(SearchListener(vm::newReportFilter))
    }

    override fun setUpObservers(vm: ReportViewModel) {

    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_list, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////        adapter = ReportListAdapter(this)
////        binding.list.adapter = adapter
////        ItemTouchHelper(CItemTouchHelperCallback(adapter, true, false)).attachToRecyclerView(binding.list)
//        binding.add.setOnClickListener(this::newReport)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = (activity as IViewModelActivity).viewModel as ReportListViewModel
//        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
////        viewModel.reports.observe(this,
////            Observer {
////                adapter.submitList(it)
//////                adapter.setItems(it)
////                binding.refresh.isRefreshing = false
////            }
////        )
////        binding.refresh.setOnRefreshListener { viewModel.refresh() }
//    }
//

//

//
//    @Suppress("UNUSED_PARAMETER")
//    private fun newReport(view: View) {
////        viewModel.newReport()
//    }
//
//    override fun delete(id: UUID) {
////        viewModel.deleteReport(id)
//    }
//
//    override fun click(id: UUID) {
////        viewModel.selectReport(id)
//    }
//
//    interface ReportListViewModel {
//        //        val reports: Observable<PagedList<ReportHeader>>
////        val reports: LiveData<PagedList<ReportHeader>>
////
////        fun newReport()
////        fun refresh()
////        fun deleteReport(id: UUID)
////        fun selectReport(id: UUID)
////        fun newReportListFilter(text: String)
//    }

}