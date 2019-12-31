package gr.blackswamp.damagereports.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import java.util.*

@Dao
interface BrandDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBrand(brand: BrandEntity)

    @Query("DELETE FROM brands WHERE id = :id")
    suspend fun deleteBrandById(id: UUID)

    @Query("SELECT count(*) FROM brands WHERE NOT deleted")
    suspend fun count(): Int

    @Query("SELECT * FROM brands WHERE NOT deleted AND (:filter = '' OR lower(name) LIKE '%' || lower(:filter) || '%') ORDER BY name ")
    fun loadBrands(filter: String = ""): DataSource.Factory<Int, BrandEntity>

}