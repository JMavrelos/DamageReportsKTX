package gr.blackswamp.damagereports.ui.reports.adapters

import java.util.*

interface ReportListAction {
    fun delete(id: UUID)
    fun select(id: UUID)
    fun edit(id: UUID)
}
