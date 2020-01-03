package gr.blackswamp.damagereports.data.db.dao

import androidx.paging.DataSource
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

    @Query("SELECT * FROM models WHERE NOT deleted AND (:filter = '' OR lower(name) LIKE '%' || lower(:filter) || '%') ORDER BY name ")
    fun loadModels(filter: String): DataSource.Factory<Int, ModelEntity>

    @Query("SELECT * FROM models WHERE id = :id")
    suspend fun loadModelById(id: UUID): ModelEntity?

    @Query("DELETE FROM models WHERE id = :id")
    suspend fun deleteModelById(id: UUID)
}