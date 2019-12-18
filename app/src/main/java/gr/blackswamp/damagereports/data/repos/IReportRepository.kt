package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*

interface IReportRepository {
    val darkTheme: Boolean
    val darkThemeLive: LiveData<Boolean>

    suspend fun loadReports(filter: String, skip: Int): Response<List<ReportHeaderEntity>>
    suspend fun newReport(name: String, description: String, brandId: UUID, modelId: UUID): Throwable?
    /**
     * Changes the current theme
     * @return true if it switched to dark theme or false for light theme
     */
    suspend fun switchTheme()
}
