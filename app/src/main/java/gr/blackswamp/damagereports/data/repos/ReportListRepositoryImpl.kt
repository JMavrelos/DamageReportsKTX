package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.logic.model.ReportData
import gr.blackswamp.damagereports.logic.model.ReportHeaderData
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class ReportListRepositoryImpl : BaseRepositoryImpl(), ReportListRepository, KoinComponent {
    private val db: AppDatabase by inject()
    override val themeSetting: ThemeSetting
        get() = prefs.themeSetting


    override suspend fun clearDeleted(): Response<Unit> {
        return try {
            db.globalDao.clearUnused()
            Response.success()
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override fun getReportHeaders(filter: String): Response<DataSource.Factory<Int, ReportHeaderData>> {
        return try {
            Response.success(db.reportDao.loadReportHeaders(filter).map(ReportHeaderEntity::toData))
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override suspend fun loadReport(id: UUID): Response<ReportData> {
        return try {
            val report = db.reportDao.loadReportById(id) ?: return Response.failure(getString(R.string.error_report_not_found, id))
            val brand = db.brandDao.loadBrandById(report.brand) ?: return Response.failure(getString(R.string.error_brand_not_found, report.brand))
            val model = db.modelDao.loadModelById(report.model) ?: return Response.failure(getString(R.string.error_model_not_found, report.model))
            if (model.brand != brand.id) return Response.failure(getString(R.string.error_invalid_model_brand))
            Response.success(report.toData(brand, model))
        } catch (t: Throwable) {
            Response.failure(getString(R.string.error_loading_report, id), t)
        }
    }

    override suspend fun deleteReport(id: UUID): Response<Unit> {
        return try {
            val affected = db.reportDao.flagReportDeleted(id)
            if (affected == 0)
                return Response.failure(getString(R.string.error_report_not_found, id))
            Response.success()
        } catch (t: Throwable) {
            return Response.failure(getString(R.string.error_deleting, (t.message ?: t::class.java.name)), t)
        }
    }

    override suspend fun restoreReport(id: UUID): Response<Unit> {
        return try {
            val affected = db.reportDao.unFlagReportDeleted(id)
            if (affected == 0)
                return Response.failure(getString(R.string.error_no_deleted_report, id))
            Response.success()
        } catch (t: Throwable) {
            return Response.failure(getString(R.string.error_un_deleting, (t.message ?: t::class.java.name)), t)
        }
    }

    override fun setTheme(themeSetting: ThemeSetting) {
        prefs.themeSetting = themeSetting
    }
}

