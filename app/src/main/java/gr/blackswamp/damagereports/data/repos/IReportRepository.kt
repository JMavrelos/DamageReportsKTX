package gr.blackswamp.damagereports.data.repos

import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*

interface IReportRepository {
    suspend fun loadReports(filter: String, skip: Int): Response<List<ReportHeaderEntity>>
    suspend fun newReport(name: String, description: String, brandId: UUID, modelId: UUID): Throwable?
}
