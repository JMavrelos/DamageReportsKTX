package gr.blackswamp.damagereports.data.repos

import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*

class ReportRepository(val db: IDatabase) : IReportRepository {
    override suspend fun loadReports(filter: String, skip: Int): Response<List<ReportHeaderEntity>> {
        return try {
            Response.success(db.reportDao.loadReportHeaders(filter))
        } catch (e: Throwable) {
            Response.failure(e)
        }
    }

    override suspend fun newReport(name: String, description: String, brandId: UUID, modelId: UUID): Throwable? {
        return try {
            val entity = ReportEntity(UUID.randomUUID(), name, description, brandId, modelId)
            db.reportDao.saveReport(entity)
            null
        } catch (t: Throwable) {
            t
        }
    }
}