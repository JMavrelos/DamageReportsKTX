package gr.blackswamp.damagereports.logic.commands

import gr.blackswamp.damagereports.ui.model.Report

sealed class ReportListCommand {
    class ShowReport(val report: Report, val inEditMode: Boolean) : ReportListCommand()
}