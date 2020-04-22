package gr.blackswamp.damagereports.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.widget.CItemTouchAdapter
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.model.ReportHeader

@Suppress("unused")
class ReportListAdapter : PagedListAdapter<ReportHeader, ReportListAdapter.ReportHeaderViewHolder>(DIFF_CALLBACK), CItemTouchAdapter {
    companion object {
        private const val TAG = "ReportListAdapter"
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReportHeader>() {
            override fun areItemsTheSame(oldItem: ReportHeader, newItem: ReportHeader): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ReportHeader, newItem: ReportHeader): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.date == newItem.date
                        && oldItem.description == newItem.description
                        && oldItem.name == newItem.name
            }

        }

        private val LABEL_ID = EmptyUUID
        private const val LABEL = 0
        private const val HEADER = 1
    }

    private var listener: ListAction? = null

    fun setListener(listener: ListAction) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int = if (getItem(position)?.id == LABEL_ID) LABEL else HEADER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportHeaderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> HeaderViewHolder(inflater.inflate(R.layout.list_item_report_header, parent, false))
            else -> LabelViewHolder(inflater.inflate(R.layout.list_item_report_label, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ReportHeaderViewHolder, position: Int) {
        getItem(position)?.let {
            holder.update(it)
        }
    }


    //region events
    override fun onItemMove(fromPosition: Int, toPosition: Int) = Unit //we do not handle rearranging

    override fun onItemMoveFinished(fromPosition: Int, toPosition: Int) =
        Unit //we do not handle rearranging

    override fun onItemDismissed(position: Int) {
        getItem(position)?.let {
            if (it.id != LABEL_ID)
                listener?.delete(it.id)
        }
    }

    override fun allowDismiss(position: Int): Boolean = (getItem(position)?.id != LABEL_ID)

    private fun onItemClick(position: Int) {
        getItem(position)?.let {
            listener?.select(it.id)
        }
    }

    private fun onLongItemClick(position: Int) {
        getItem(position)?.let {
            listener?.edit(it.id)
        }
    }
    //endregion

    //region view holders
    abstract class ReportHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun update(header: ReportHeader)
    }

    private inner class HeaderViewHolder(view: View) : ReportHeaderViewHolder(view) {
        val name: TextView = view.findViewById(R.id.report_name)
        val description: TextView = view.findViewById(R.id.report_description)
        override fun update(header: ReportHeader) {
            name.text = header.name
            description.text = header.description
        }

        init {
            itemView.setOnClickListener {
                this@ReportListAdapter.onItemClick(adapterPosition)
            }
            itemView.setOnLongClickListener {
                this@ReportListAdapter.onLongItemClick(adapterPosition)
                true
            }
        }
    }

    private class LabelViewHolder(view: View) : ReportHeaderViewHolder(view) {
        val label: TextView = view as TextView
        override fun update(header: ReportHeader) {
            label.text = header.name
        }
    }
    //endregion
}

