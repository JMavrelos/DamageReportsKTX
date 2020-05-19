package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.logic.model.BrandData
import java.util.*

interface BrandRepository : BaseRepository {
    /** gets a list of brands whose name matches a [filter] */
    fun getBrands(filter: String): Response<DataSource.Factory<Int, BrandData>>

    /** loads the data of a brand with [id] */
    suspend fun getBrand(id: UUID): Response<BrandData>

    /** creates a new brand with the specified [name] */
    suspend fun newBrand(name: String): Response<Unit>

    /** updates the [name] of the brand with an [id] */
    suspend fun updateBrand(id: UUID, name: String): Response<Unit>

    /** flags a brand with [id] as deleted */
    suspend fun deleteBrand(id: UUID): Response<Unit>

    /** restores a deleted brand with [id] */
    suspend fun restoreBrand(id: UUID): Response<Unit>
}