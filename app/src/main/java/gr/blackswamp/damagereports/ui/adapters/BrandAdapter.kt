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
import gr.blackswamp.damagereports.ui.model.Brand

@Suppress("unused")
class BrandAdapter : PagedListAdapter<Brand, BrandAdapter.BrandViewHolder>(DIFF_CALLBACK), CItemTouchAdapter {
    companion object {
        private const val TAG = "BrandAdapter"
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Brand>() {
            override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder =
        BrandViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_make, parent, false))

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
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
    inner class BrandViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)

        fun update(brand: Brand) {
            name.text = brand.name
        }

        init {
            itemView.setOnClickListener {
                this@BrandAdapter.onItemClick(adapterPosition)
            }
            itemView.setOnLongClickListener {
                this@BrandAdapter.onLongItemClick(adapterPosition)
                true
            }
        }
    }
//endregion
}

