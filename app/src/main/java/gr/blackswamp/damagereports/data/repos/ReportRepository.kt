package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import gr.blackswamp.damagereports.data.prefs.IPreferences
import kotlinx.coroutines.withContext
import java.util.*

class ReportRepository(private val db: IDatabase, private val prefs: IPreferences, val dispatchers: IDispatchers) : IReportRepository {
    override val darkTheme: Boolean
        get() = prefs.darkTheme
    override val darkThemeLive: LiveData<Boolean> = prefs.darkThemeLive

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

    override suspend fun switchTheme() {
        withContext(dispatchers.IO) {
            prefs.darkTheme = !prefs.darkTheme
        }
    }
}