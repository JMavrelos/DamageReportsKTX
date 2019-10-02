package gr.blackswamp.damagereports.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*


@Dao
interface ReportDao {
    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun loadReport(id: UUID): ReportEntity

    @Query(
        "SELECT id, name, description,created " +
                "FROM   (SELECT id, name, description, created, created AS sort " +
                "        FROM   reports " +
                "        WHERE  :filter = '' " +
                "                OR name LIKE '%' || :filter || '%' " +
                "                OR description LIKE '%' || :filter || '%' " +
                "        UNION " +
                "        SELECT '00000000-0000-0000-0000-000000000000' as id, Substr(created, 0, 9) as name, '' as description, Substr(created, 0, 9) || '000000' AS created, Substr(created, 0, 9) || '256060' AS sort " +
                "        FROM   reports " +
                "        WHERE  :filter = '' " +
                "                OR name LIKE '%' || :filter || '%' " +
                "                OR description LIKE '%' || :filter || '%' " +
                "        GROUP  BY Substr(created, 0, 9)) " +
                "ORDER  BY sort DESC ")

    suspend fun loadReportHeaders(filter: String = ""): List<ReportHeaderEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveReport(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReportById(id: UUID)

    @Query("SELECT count(*) FROM reports ")
    suspend fun count(): Int
}