package gr.blackswamp.damagereports.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import gr.blackswamp.damagereports.data.db.entities.DamageEntity
import gr.blackswamp.damagereports.data.db.entities.ReportDamageEntity
import java.util.*


@Dao
interface DamageDao {
    @Insert
    suspend fun insertDamage(damage: DamageEntity)

    @Query("DELETE FROM damages WHERE id = :id")
    suspend fun deleteDamageById(id: UUID)

    @Query(
        "SELECT damages.id AS id," +
                "       damages.description AS name," +
                "       3 AS pictures," +
                "       count( * ) AS parts," +
                "       sum(damage_parts.quantity * parts.price) AS cost" +
                "  FROM damages" +
                "       LEFT JOIN" +
                "       damage_parts ON damages.id = damage_parts.damage" +
                "       LEFT JOIN" +
                "       parts ON damage_parts.part = parts.id" +
                " WHERE report = :id" +
                " GROUP BY damages.id," +
                "          damages.description"
    )
    fun loadDamageHeadersForReport(id: UUID): DataSource.Factory<Int, ReportDamageEntity>
}