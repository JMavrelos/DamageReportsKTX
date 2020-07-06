package gr.blackswamp.damagereports.data.db.dao

import androidx.paging.DataSource
import androidx.room.*
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*


@Dao
interface ReportDao {
    @Query("select * from reports where id = :id")
    suspend fun loadReportById(id: UUID): ReportEntity?

    @Query(
        "select id, name, description,created " +
                "from   (select id, name, description, created, created as sort " +
                "        from   reports " +
                "        where  deleted = 0 " +
                "               and (:filter = '' " +
                "                     or name like '%' || :filter || '%' " +
                "                     or description like '%' || :filter || '%' )" +
                "        union " +
                "        select '00000000-0000-0000-0000-000000000000' as id" +
                "                     , substr(created, 1, 4) || '-' ||  substr(created, 5, 2) || '-' ||  substr(created, 7, 2) as name" +
                "                     , '' as description" +
                "                     , Substr(created, 1, 8) || '000000000' as created" +
                "                     , substr(created, 1, 8) || '256060999' AS sort " +
                "        from   reports " +
                "        where  deleted = 0 " +
                "               and (:filter = '' " +
                "                or name like '%' || :filter || '%' " +
                "                or description like '%' || :filter || '%') " +
                "        group  by substr(created, 0, 9)) " +
                "order  by sort desc "
    )
    fun loadReportHeaders(filter: String): DataSource.Factory<Int, ReportHeaderEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReport(report: ReportEntity)

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Query("UPDATE reports SET deleted = 1 WHERE id = :id AND deleted = 0")
    suspend fun flagReportDeleted(id: UUID): Int

    @Query("UPDATE reports SET deleted = 0 WHERE id = :id AND deleted = 1")
    suspend fun unFlagReportDeleted(id: UUID): Int

    @Query("SELECT count(*) FROM reports order by created")
    suspend fun count(): Int

}