package gr.blackswamp.damagereports.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import gr.blackswamp.damagereports.data.db.entities.PartEntity
import java.util.*


@Dao
interface PartDao {

    @Insert
    suspend fun insertPart(part: PartEntity)

    @Update
    suspend fun updatePart(part: PartEntity): Int

    @Query("UPDATE parts SET deleted = 1 WHERE id = :id AND deleted = 0")
    suspend fun flagPartDeleted(id: UUID): Int

}