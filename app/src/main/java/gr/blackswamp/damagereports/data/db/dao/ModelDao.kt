package gr.blackswamp.damagereports.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gr.blackswamp.damagereports.data.db.entities.ModelEntity
import java.util.*

@Dao
interface ModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveModel(model: ModelEntity)

    @Query("SELECT * FROM models WHERE NOT deleted AND (:filter = '' OR lower(name) LIKE '%' || lower(:filter) || '%') ORDER BY name LIMIT :skip, :take  ")
    suspend fun searchModels(filter: String, skip: Int = 0, take: Int = -1): List<ModelEntity>

    @Query("DELETE FROM models WHERE id = :id")
    suspend fun deleteModelById(id: UUID)
}