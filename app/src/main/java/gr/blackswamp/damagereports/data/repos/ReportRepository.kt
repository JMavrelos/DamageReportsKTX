package gr.blackswamp.damagereports.data.repos

import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity

class ReportRepository(val db: IDatabase) : IReportRepository {
    override suspend fun loadReports(filter: String, i: Int): Response<List<ReportHeaderEntity>> {
        return try {
            Response.success(db.reportDao.loadReportHeaders())
        } catch (e: Throwable) {
            Response.failure(e)
        }
    }

}