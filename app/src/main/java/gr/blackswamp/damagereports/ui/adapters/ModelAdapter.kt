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
import gr.blackswamp.damagereports.ui.model.Model

@Suppress("unused")
class ModelAdapter : PagedListAdapter<Model, ModelAdapter.ModelViewHolder>(DIFF_CALLBACK), CItemTouchAdapter {
    companion object {
        private const val TAG = "ModelAdapter"
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Model>() {
            override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
                return oldItem.id == newItem.id
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder =
        ModelViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_make, parent, false))

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
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

    //region view holder
    inner class ModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)

        fun update(model: Model) {
            name.text = model.name
        }

        init {
            itemView.setOnClickListener {
                this@ModelAdapter.onItemClick(adapterPosition)
            }
            itemView.setOnLongClickListener {
                this@ModelAdapter.onLongItemClick(adapterPosition)
                true
            }
        }
    }
    //endregion
}

