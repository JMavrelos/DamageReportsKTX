package gr.blackswamp.damagereports.data.repos

import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.logic.model.ReportData
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class ReportViewRepositoryImpl : BaseRepositoryImpl(), ReportViewRepository, KoinComponent {
    private val db: AppDatabase by inject()

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
}

