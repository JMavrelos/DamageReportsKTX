package gr.blackswamp.damagereports.ui.reports

import gr.blackswamp.damagereports.ui.base.ScreenCommand
import java.util.*

sealed class ReportCommand : ScreenCommand() {
    object ConfirmDiscard : ScreenCommand()
    object ShowBrandSelection : ScreenCommand()
    class ShowModelSelection(val brandId: UUID) : ScreenCommand()
}