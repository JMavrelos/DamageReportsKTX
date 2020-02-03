package gr.blackswamp.damagereports.ui.reports.commands

import java.util.*

sealed class ReportActivityCommand {
    object ConfirmDiscard : ReportActivityCommand()
    object ShowBrandSelection : ReportActivityCommand()
    class ShowModelSelection(val brandId: UUID) : ReportActivityCommand()
}