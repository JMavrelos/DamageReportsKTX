package gr.blackswamp.damagereports.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import gr.blackswamp.damagereports.data.db.entities.DamagePartEntity


@Dao
interface DamagePartDao {

    @Insert
    suspend fun insertDamagePart(part: DamagePartEntity)

    @Update
    suspend fun updateDamagePart(part: DamagePartEntity): Int

}