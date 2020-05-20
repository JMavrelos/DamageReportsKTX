package gr.blackswamp.damagereports.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import gr.blackswamp.damagereports.data.db.entities.DamageEntity
import java.util.*


@Dao
interface DamageDao {
    @Insert
    suspend fun insertDamage(damage: DamageEntity)

    @Query("DELETE FROM damages WHERE id = :id")
    suspend fun deleteDamageById(id: UUID)

    @Query("SELECT * FROM damages WHERE report = :id")
    suspend fun loadDamagesForReport(id: UUID): List<DamageEntity>
}