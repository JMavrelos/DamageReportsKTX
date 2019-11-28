package gr.blackswamp.core.widget

interface CItemTouchAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemMoveFinished(fromPosition: Int, toPosition: Int)

    fun onItemDismissed(position: Int)

    fun allowDismiss(position: Int): Boolean
}
