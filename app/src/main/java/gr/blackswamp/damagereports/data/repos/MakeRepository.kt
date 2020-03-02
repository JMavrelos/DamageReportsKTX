package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import java.util.*

interface MakeRepository : BaseRepository {
    fun getBrands(filter: String, withId: UUID?): Response<DataSource.Factory<Int, BrandEntity>>
    suspend fun newBrand(name: String): Response<Unit>
    suspend fun updateBrand(id: UUID, name: String): Response<Unit>
}