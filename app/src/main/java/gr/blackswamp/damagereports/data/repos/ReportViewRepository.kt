package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.logic.model.ReportDamageData
import gr.blackswamp.damagereports.logic.model.ReportData
import java.util.*

interface ReportViewRepository : BaseRepository {
    /**
     * loads all data needed to display a report
     * @return the report or the error thrown when loading it
     */
    suspend fun loadReport(id: UUID): Response<ReportData>

    /**
     * loads all damages for the specific report
     * @return a paged list of the damages or the error
     */
    fun getDamageHeadersList(id: UUID): Response<DataSource.Factory<Int, ReportDamageData>>
}
