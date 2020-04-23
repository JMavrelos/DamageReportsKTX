package gr.blackswamp.damagereports.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import gr.blackswamp.damagereports.data.db.entities.ModelEntity
import java.util.*

@Dao
interface ModelDao {

    @Insert
    suspend fun insertModel(model: ModelEntity)

    @Update
    suspend fun updateModel(model: ModelEntity): Int

    @Query("SELECT * FROM models WHERE NOT deleted AND (:filter = '' OR lower(name) LIKE '%' || lower(:filter) || '%') AND brand = :brandId ORDER BY name ")
    fun loadModels(brandId: UUID, filter: String = ""): DataSource.Factory<Int, ModelEntity>

    @Query("SELECT * FROM models WHERE id = :id")
    suspend fun loadModelById(id: UUID): ModelEntity?

    @Query("DELETE FROM models WHERE id = :id")
    suspend fun deleteModelById(id: UUID)
}