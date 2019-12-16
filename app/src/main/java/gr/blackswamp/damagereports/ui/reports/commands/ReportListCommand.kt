package gr.blackswamp.damagereports.ui.reports.commands

import gr.blackswamp.damagereports.ui.reports.model.ReportHeader

sealed class ReportListCommand() {
     class SetReports(val reports: List<ReportHeader>) : ReportListCommand()
     class AddReports(val reports: List<ReportHeader>) : ReportListCommand()
     class AddReport(val report: ReportHeader) : ReportListCommand()
     class UpdateReport(val report: ReportHeader) : ReportListCommand()
     class DeleteReport(val report: ReportHeader) : ReportListCommand()

}