package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*

interface IReportRepository {
    val darkTheme: Boolean
    val darkThemeLive: LiveData<Boolean>


    suspend fun newReport(name: String, description: String, brandId: UUID, modelId: UUID): Throwable?
    /**
     * Changes the current theme
     * @return true if it switched to dark theme or false for light theme
     */
    suspend fun switchTheme()

    /**
     * gets a list of report headers according to a filter
     * @return a data source with the report header entities
     */
    fun getReportHeaders(filter: String): DataSource.Factory<Int, ReportHeaderEntity>

    /**
     * deletes a report with a specific id
     */
    suspend fun deleteReport(id: UUID): Throwable?
}
