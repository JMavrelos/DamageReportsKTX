package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.logic.model.BrandData
import java.util.*

interface BrandRepository : BaseRepository {
    fun getBrands(filter: String): Response<DataSource.Factory<Int, BrandData>>
    suspend fun newBrand(name: String): Response<Unit>
    suspend fun updateBrand(id: UUID, name: String): Response<Unit>
    suspend fun getBrand(id: UUID): Response<BrandData>
}