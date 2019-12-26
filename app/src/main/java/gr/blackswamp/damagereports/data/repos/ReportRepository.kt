package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import gr.blackswamp.damagereports.data.prefs.IPreferences
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.random.Random

class ReportRepository(
    private val db: IDatabase,
    private val prefs: IPreferences,
    val dispatchers: IDispatchers
) : IReportRepository {
    override val darkTheme: Boolean
        get() = prefs.darkTheme
    override val darkThemeLive: LiveData<Boolean> = prefs.darkThemeLive

    override suspend fun newReport(
        name: String,
        description: String,
        brandId: UUID,
        modelId: UUID
    ): Throwable? {
        return try {
            val date =
                Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, Random.nextInt(8) - 4) }
            val entity =
                ReportEntity(UUID.randomUUID(), name, description, brandId, modelId, date.time)
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

    override fun getReportHeaders(filter: String): Response<DataSource.Factory<Int, ReportHeaderEntity>> {
        return try {
            Response.success(db.reportDao.reportHeaders(filter))
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override suspend fun deleteReport(id: UUID): Throwable? {
        return try {
            db.reportDao.deleteReportById(id)
            null
        } catch (t: Throwable) {
            t
        }
    }
}