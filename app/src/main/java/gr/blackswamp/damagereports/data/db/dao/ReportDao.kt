package gr.blackswamp.damagereports.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*


@Dao
interface ReportDao {
    @Query("select * from reports where id = :id")
    suspend fun loadReport(id: UUID): ReportEntity

    @Query(
        "select id, name, description,created " +
                "from   (select id, name, description, created, created as sort " +
                "        from   reports " +
                "        where  :filter = '' " +
                "                or name like '%' || :filter || '%' " +
                "                or description like '%' || :filter || '%' " +
                "        union " +
                "        select '00000000-0000-0000-0000-000000000000' as id, substr(created, 0, 9) as name, '' as description, Substr(created, 0, 9) || '000000' as created, substr(created, 0, 9) || '256060' AS sort " +
                "        from   reports " +
                "        where  :filter = '' " +
                "                or name like '%' || :filter || '%' " +
                "                or description like '%' || :filter || '%' " +
                "        group  by substr(created, 0, 9)) " +
                "order  by sort desc "
    )
    fun loadReportHeaders(filter: String): DataSource.Factory<Int, ReportHeaderEntity>


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveReport(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReportById(id: UUID)

    @Query("SELECT count(*) FROM reports ")
    suspend fun count(): Int
}