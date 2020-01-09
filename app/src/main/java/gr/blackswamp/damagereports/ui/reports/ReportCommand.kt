package gr.blackswamp.damagereports.ui.reports

import gr.blackswamp.damagereports.ui.base.ScreenCommand

sealed class ReportCommand : ScreenCommand() {
    object ShowReport : ScreenCommand()
    object ConfirmDiscard : ScreenCommand()
}