package gr.blackswamp.damagereports.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import java.util.*

@Dao
interface BrandDao {

    @Insert
    suspend fun insertBrand(brand: BrandEntity)

    @Update
    suspend fun updateBrand(brand: BrandEntity): Int

    @Query("DELETE FROM brands WHERE id = :id")
    suspend fun deleteBrandById(id: UUID)

    @Query("SELECT * FROM brands WHERE id = :id")
    suspend fun loadBrandById(id: UUID): BrandEntity?

    @Query("SELECT count(*) FROM brands WHERE NOT deleted")
    suspend fun count(): Int

    @Query("SELECT * FROM brands WHERE NOT deleted AND (:filter = '' OR lower(name) LIKE '%' || lower(:filter) || '%') ORDER BY name ")
    fun loadBrands(filter: String = ""): DataSource.Factory<Int, BrandEntity>

    @Query("SELECT * FROM brands WHERE id = :id")
    fun loadBrandFactoryById(id: UUID): DataSource.Factory<Int, BrandEntity>

}