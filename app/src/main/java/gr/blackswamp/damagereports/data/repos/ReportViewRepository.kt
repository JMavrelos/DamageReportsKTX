package gr.blackswamp.damagereports.data.repos

import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.logic.model.ReportData
import java.util.*

interface ReportViewRepository : BaseRepository {
    /**
     * loads all data needed to display a report
     * @return the report or the error thrown when loading it
     */
    suspend fun loadReport(id: UUID): Response<ReportData>
}
