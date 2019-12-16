package gr.blackswamp.damagereports.reports.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.blackswamp.core.widget.CItemTouchAdapter
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.reports.model.ReportHeader
import java.util.*

class ReportListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), CItemTouchAdapter {
    companion object {
        private const val TAG = "ReportListAdapter"
        private val EMPTY_ID = UUID(0L, 0L)
        private const val LABEL = 0
        private const val HEADER = 1
    }

    private val headers = mutableListOf<ReportHeader>()
    private var listener: OnListAction? = null
    fun setListener(listener: OnListAction) {
        this.listener = listener
    }


    override fun getItemCount(): Int = headers.size


    override fun getItemViewType(position: Int): Int =
        if (headers[position].id == EMPTY_ID) LABEL else HEADER


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> ReportHeaderViewHolder(inflater.inflate(R.layout.list_item_report, parent, false))
            else -> ReportLabelViewHolder(inflater.inflate(R.layout.list_item_report_label, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reportItem = headers[position]
        when (getItemViewType(position)) {
            HEADER -> (holder as ReportHeaderViewHolder).update(reportItem)
            LABEL -> (holder as ReportLabelViewHolder).update(reportItem)
        }
    }

    //region touch events
    override fun onItemMove(fromPosition: Int, toPosition: Int) =
        Unit //we do not handle rearranging

    override fun onItemMoveFinished(fromPosition: Int, toPosition: Int) =
        Unit //we do not handle rearranging

    override fun onItemDismissed(position: Int) {
        headers[position].let {
            if (it.id != EMPTY_ID)
                listener?.delete(it.id)
        }
    }

    override fun allowDismiss(position: Int): Boolean = (headers[position].id != EMPTY_ID)

    fun onItemClick(position: Int) {
        listener?.click(headers[position].id)
    }
    //endregion


    inner class ReportHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.report_name)
        val description: TextView = view.findViewById(R.id.report_description)
        fun update(reportItem: ReportHeader) {
            name.text = reportItem.name
            description.text = reportItem.description
        }

        init {
            itemView.setOnClickListener {
                this@ReportListAdapter.onItemClick(adapterPosition)
            }
        }
    }

    class ReportLabelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view as TextView
        fun update(reportItem: ReportHeader) {
            label.text = reportItem.description
        }


    }
}