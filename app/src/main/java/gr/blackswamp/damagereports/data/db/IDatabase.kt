package gr.blackswamp.damagereports.data.db

import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*

interface IDatabase {
    suspend fun loadReportHeaders(filter: String = ""): List<ReportHeaderEntity>

    suspend fun saveReport(report: ReportEntity)

    suspend fun deleteReportById(id: UUID)
}