package gr.blackswamp.damagereports.ui.reports

import gr.blackswamp.damagereports.ui.base.commands.ScreenCommand

sealed class ReportCommands : ScreenCommand() {
    object ShowReport : ScreenCommand()
}