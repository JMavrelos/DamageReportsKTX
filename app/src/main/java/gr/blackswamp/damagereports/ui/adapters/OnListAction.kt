package gr.blackswamp.damagereports.ui.adapters

import java.util.*

interface OnListAction {
    fun delete(id: UUID)
    fun click(id: UUID)
}
