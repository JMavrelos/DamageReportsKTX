package gr.blackswamp.damagereports.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import gr.blackswamp.core.widget.CItemTouchAdapter
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.ListItemReportBinding
import gr.blackswamp.damagereports.databinding.ListItemReportLabelBinding
import gr.blackswamp.damagereports.ui.model.ReportHeader
import java.util.*

//class ReportListAdapter(private val listener: OnListAction) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), CItemTouchAdapter {
//    companion object {
//        private const val TAG = "ReportListAdapter"
//        private val EMPTY_ID = UUID(0L,0L)
//        private class ItemCallback : DiffUtil.ItemCallback<ReportHeader>() {
//            override fun areItemsTheSame(oldItem: ReportHeader, newItem: ReportHeader): Boolean = oldItem.id == newItem.id
//            override fun areContentsTheSame(oldItem: ReportHeader, newItem: ReportHeader): Boolean = oldItem.equals(newItem)
//        }
//
//        private const val LABEL = 0
//        private const val HEADER = 1
//    }
//
//    override fun getItemViewType(position: Int): Int = if (getItem(position)?.id ?: EMPTY_ID == EMPTY_ID) LABEL else HEADER
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        return when (viewType) {
//            HEADER -> ReportHeaderViewHolder(DataBindingUtil.inflate(inflater, R.layout.list_item_report, parent, false))
//            else -> ReportLabelViewHolder(DataBindingUtil.inflate(inflater, R.layout.list_item_report_label, parent, false))
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val reportItem = getItem(position)
//        when (getItemViewType(position)) {
//            HEADER -> {
//                (holder as ReportHeaderViewHolder).binding.apply {
//                    report = reportItem
//                    executePendingBindings()
//                }
//            }
//            LABEL -> {
//                (holder as ReportLabelViewHolder).binding.apply {
//                    date = reportItem?.name
//                    executePendingBindings()
//                }
//            }
//        }
//    }
//
//    //region touch events
//    override fun onItemMove(fromPosition: Int, toPosition: Int) = Unit //we do not handle rearranging
//
//    override fun onItemMoveFinished(fromPosition: Int, toPosition: Int) = Unit //we do not handle rearranging
//
//    override fun onItemDismissed(position: Int) {
//        getItem(position)?.let {
//            if (it.id != EMPTY_ID) {
//                listener.delete(it.id)
//            }
//        }
//    }
//
//    override fun allowDismiss(position: Int): Boolean = (getItem(position)?.id ?: EMPTY_ID != EMPTY_ID)
//
//    fun onItemClick(report: ReportHeader?) = report?.let { listener.click(it.id) }
//    //endregion
//
//
//    inner class ReportHeaderViewHolder(val binding: ListItemReportBinding) : RecyclerView.ViewHolder(binding.root) {
//        init {
//            itemView.setOnClickListener {
//                this@ReportListAdapter.onItemClick(binding.report)
//            }
//        }
//    }
//
//    class ReportLabelViewHolder(val binding: ListItemReportLabelBinding) : RecyclerView.ViewHolder(binding.root)
//}