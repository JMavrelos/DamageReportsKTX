package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
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
    fun getReportHeaders(filter: String): Response<DataSource.Factory<Int, ReportHeaderEntity>>

    /**
     * flags a report with a specific id as deleted
     * @return null if there was no error otherwise a message
     */
    suspend fun deleteReport(id: UUID): String?

    /**
     * unflags a report with a specific id as deleted
     * @return null if there was no error otherwise a message
     */
    suspend fun unDeleteReport(id:UUID) : String?
}
