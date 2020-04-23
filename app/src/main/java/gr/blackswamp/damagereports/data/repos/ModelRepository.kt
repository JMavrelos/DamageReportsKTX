package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.logic.model.ModelData
import java.util.*

interface ModelRepository : BaseRepository {
    fun getModels(parent: UUID, filter: String): Response<DataSource.Factory<Int, ModelData>>
    suspend fun newModel(name: String, brandId: UUID): Response<Unit>
    suspend fun updateModel(id: UUID, brandId: UUID, name: String): Response<Unit>

}