package gr.blackswamp.damagereports.ui.reports.adapters

import java.util.*

interface onReportListAction {
    fun delete(id: UUID)
    fun click(id: UUID)
}
