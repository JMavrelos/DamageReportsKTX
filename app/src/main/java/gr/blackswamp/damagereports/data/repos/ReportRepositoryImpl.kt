package gr.blackswamp.damagereports.data.repos

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.vms.ReportData
import gr.blackswamp.damagereports.vms.ReportHeaderData
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.random.Random

class ReportRepositoryImpl : ReportRepository, KoinComponent {
    private val db: AppDatabase by inject()
    private val prefs: Preferences by inject()
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

    override fun getReportHeaders(filter: String): Response<DataSource.Factory<Int, ReportHeaderData>> {
        return try {
            Response.success(db.reportDao.loadReportHeaders(filter)
                .map { entity -> entity.toData() })
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override suspend fun loadReport(id: UUID): Response<ReportData> {
        return withContext(dispatchers.IO) {
            try {
                val report = db.reportDao.loadReportById(id) ?: return@withContext Response.failure<ReportData>(getString(R.string.error_report_not_found, id))
                val brand = db.brandDao.loadBrandById(report.brand) ?: return@withContext Response.failure<ReportData>(getString(R.string.error_brand_not_found, report.brand))
                val model = db.modelDao.loadModelById(report.model) ?: return@withContext Response.failure<ReportData>(getString(R.string.error_model_not_found, report.model))
                if (model.brand != brand.id) return@withContext Response.failure<ReportData>(getString(R.string.error_invalid_model_brand))
                Response.success(report.toData(brand, model))
            } catch (t: Throwable) {
                Response.failure<ReportData>(getString(R.string.error_loading_report, id), t)
            }
        }
    }

    override suspend fun deleteReport(id: UUID): Response<Unit> {
        return try {
            val affected = withContext(dispatchers.IO) { db.reportDao.flagReportDeleted(id) }
            if (affected == 0)
                return Response.failure(getString(R.string.error_report_not_found, id))
            Response.success()
        } catch (t: Throwable) {
            return Response.failure(getString(R.string.error_deleting, (t.message ?: t::class.java.name)), t)
        }
    }

    override suspend fun unDeleteReport(id: UUID): Response<Unit> {
        return try {
            val affected = withContext(dispatchers.IO) { db.reportDao.unFlagReportDeleted(id) }
            if (affected == 0)
                return Response.failure(getString(R.string.error_no_deleted_report, id))
            Response.success()
        } catch (t: Throwable) {
            return Response.failure(getString(R.string.error_un_deleting, (t.message ?: t::class.java.name)), t)
        }
    }

    protected fun getString(@StringRes resId: Int): String = application.getString(resId)
    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String = application.getString(resId, *formatArgs)
}

