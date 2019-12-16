package gr.blackswamp.damagereports.data.repos

import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity

interface IReportRepository {
    suspend fun loadReports(filter: String, i: Int): Response<List<ReportHeaderEntity>>

}
