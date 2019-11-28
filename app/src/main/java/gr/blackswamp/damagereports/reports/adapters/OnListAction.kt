package gr.blackswamp.damagereports.reports.adapters

import java.util.*

interface OnListAction {
    fun delete(id: UUID)
    fun click(id: UUID)
}
