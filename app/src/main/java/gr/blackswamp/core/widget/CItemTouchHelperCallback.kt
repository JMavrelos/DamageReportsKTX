package gr.blackswamp.core.widget

import android.graphics.Canvas
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Callback class used by [ItemTouchHelper]
 * It received messages from [ItemTouchHelper] and sends them to the attached [adapter]
 * It is instantiated with [allowSwipe] if we allow swipe and [allowDrag] if we wish to allow drag and drop
 */
class CItemTouchHelperCallback(private val adapter: CItemTouchAdapter, private val allowSwipe: Boolean, private val allowDrag: Boolean) : ItemTouchHelper.Callback() {

    companion object {
        const val ALPHA_FULL = 1f
    }

    private var dragFrom = -1
    private var dragTo = -1

    /** overriden function that disables long press to drag*/
    override fun isLongPressDragEnabled(): Boolean {

        return false //we will use a long press to drag
    }

    /** overriden function that sets up if we will allow swiping*/
    override fun isItemViewSwipeEnabled(): Boolean {
        return allowSwipe //used to declare if we will allow view swipe
    }

    /** this will tell the [ItemTouchHelper] what kind of movement represents a drag and a swipe*/
    override fun getMovementFlags(recycler: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags = 0
        var swipeFlags = 0

        if (recycler.layoutManager is GridLayoutManager) { //if we use a grid layout manager, then we only allow movement, not swiping
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        } else if (recycler.layoutManager is LinearLayoutManager) { //if we use a linear layout manager, then we check its direction to see what we allow
            val manager = recycler.layoutManager as LinearLayoutManager
            if (manager.orientation == LinearLayoutManager.VERTICAL) { //if the manager is vertical then dragging is allowed up and down and swiping is from left to right
                dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                swipeFlags = ItemTouchHelper.END
            } else if (manager.orientation == LinearLayoutManager.HORIZONTAL) { //if the manager is horizontal then draging is allowed left and right and swiping is upwards
                dragFlags = ItemTouchHelper.START or ItemTouchHelper.END
                swipeFlags = ItemTouchHelper.UP
            }
        }
        if (!allowDrag) //if we don't allow dragging then remove the drag directions
            dragFlags = 0
        if (!allowSwipe || !adapter.allowDismiss(viewHolder.adapterPosition)) //if we don't allow swiping then remove the swipe diretions
            swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    /** this is triggered from the [ItemTouchHelper] it is used to send the attached [adapter] the message that the user has moved an item */
    override fun onMove(recyclerView: RecyclerView, from: RecyclerView.ViewHolder, to: RecyclerView.ViewHolder): Boolean {
        val fromPosition: Int = from.adapterPosition //the position the drag starts from
        if (dragFrom == -1) //if there was another drag before the previous one finished we continue the existing drag
            dragFrom = fromPosition //otherwise we start a new drag
        dragTo = to.adapterPosition //the position the drag is currently in
        adapter.onItemMove(fromPosition, dragTo) //signal the adapter that an item has moved
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (!allowSwipe)
            return
        adapter.onItemDismissed(viewHolder.adapterPosition)
    }

    /** used to change the alpha of the [viewHolder] so that the swiping changes its color */
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        //if wea are swiping remove the alpha of the view
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width //the new alpha value darkens as the child moves towards the exit
            viewHolder.itemView.alpha = alpha //change the alpha
            viewHolder.itemView.translationX = dX //draw it with a translation (i.e. make 0,0 different for the view)
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    /**  reset the view in case the swiping did not go through or the drag has finished*/
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = ALPHA_FULL
        (viewHolder as? CItemTouchViewHolder)?.onItemEndDrag()
        if (dragFrom != -1 && dragTo != -1)
            adapter.onItemMoveFinished(dragFrom, dragTo)
        dragFrom = -1
        dragTo = -1
    }

    /** used by the [ItemTouchHelper] to signal the adapter's viewholder that the drag has started */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder is CItemTouchViewHolder) {
            (viewHolder as CItemTouchViewHolder).onItemStartDrag()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }
}
