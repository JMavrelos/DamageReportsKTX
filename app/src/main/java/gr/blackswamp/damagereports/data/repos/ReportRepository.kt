package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.vms.ReportData
import gr.blackswamp.damagereports.vms.ReportHeaderData
import java.util.*

interface ReportRepository : BaseRepository {

    suspend fun newReport(name: String, description: String, brandId: UUID, modelId: UUID): Throwable?

    /**
     * gets a make_list of report headers according to a filter
     * @return a data source with the report header entities
     */
    fun getReportHeaders(filter: String): Response<DataSource.Factory<Int, ReportHeaderData>>

    /**
     * flags a report with a specific id as deleted
     * @return null if there was no error otherwise a message
     */
    suspend fun deleteReport(id: UUID): Response<Unit>

    /**
     * unflags a report with a specific id as deleted
     * @return null if there was no error otherwise a message
     */
    suspend fun unDeleteReport(id: UUID): Response<Unit>

    /**
     * loads all data needed to display a report
     * @return the report or the error thrown when loading it
     */
    suspend fun loadReport(id: UUID): Response<ReportData>

    /**
     * changes the current application's theme
     */
    fun setTheme(themeSetting: ThemeSetting)
}
