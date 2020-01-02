package gr.blackswamp.damagereports.data.repos

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import gr.blackswamp.damagereports.data.prefs.IPreferences
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.random.Random

class ReportRepository : IReportRepository, KoinComponent {
    private val db: IDatabase by inject()
    private val prefs: IPreferences by inject()
    private val dispatchers: IDispatchers by inject()
    private val application: Application by inject()

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
            Response.success(db.reportDao.loadReportHeaders(filter))
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override suspend fun deleteReport(id: UUID): String? {
        return try {
            val affected = db.reportDao.flagReportDeleted(id)
            if (affected == 0)
                return application.getString(R.string.error_report_not_found, id)
            null
        } catch (t: Throwable) {
            application.getString(R.string.error_deleting, (t.message ?: t::class.java.name))
        }
    }

    override suspend fun unDeleteReport(id: UUID): String? {
        return try {
            val affected = db.reportDao.unFlagReportDeleted(id)
            if (affected == 0)
                return application.getString(R.string.error_no_deleted_report,id)
            null
        }catch (t:Throwable){
            application.getString(R.string.error_un_deleting, (t.message ?: t::class.java.name))
        }
    }
}