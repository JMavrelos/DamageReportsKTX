package gr.blackswamp.damagereports.ui.reports

import java.util.*

sealed class ReportCommand {
    object ShowUndoDelete : ReportCommand()
    object ConfirmDiscard : ReportCommand()
    object ShowBrandSelection : ReportCommand()
    object ShowSettings : ReportCommand()
    class ShowModelSelection(val brandId: UUID) : ReportCommand()
}