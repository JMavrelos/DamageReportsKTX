package gr.blackswamp.damagereports.reports.fragments


import androidx.fragment.app.Fragment
import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.reports.ReportViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel


class ReportListFragment : CoreFragment<ReportViewModel>(){
    companion object {

        const val TAG = "ReportListFragment"
        fun newInstance(): Fragment = ReportListFragment()
    }
    override val vm: ReportViewModel by sharedViewModel ()
    override val layoutId: Int = R.layout.fragment_report_list



//    lateinit var binding: FragmentReportListBinding
////    lateinit var adapter: ReportListAdapter
//
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
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.report_list, menu)
//        val search = menu.findItem(R.id.search).actionView as SearchView
//        search.setOnQueryTextListener(SearchListener(this::filterChanged))
//    }
//
//    fun filterChanged(text: String, submitted: Boolean): Boolean {
////        if (submitted) {
////            viewModel.newReportListFilter(text)
//            return true
////        }
////        return false
//    }
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