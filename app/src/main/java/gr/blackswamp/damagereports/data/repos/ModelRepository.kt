package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.logic.model.ModelData
import java.util.*

interface ModelRepository : BaseRepository {
    /** gets a list of models under a specific [parent] whose name matches a [filter] */
    fun getModels(parent: UUID, filter: String): Response<DataSource.Factory<Int, ModelData>>

    /** creates a new model with the specified [name] under a [brandId]*/
    suspend fun newModel(name: String, brandId: UUID): Response<Unit>

    /** updates the [name] of a specific model with [id] under a [brandId] */
    suspend fun updateModel(id: UUID, brandId: UUID, name: String): Response<Unit>

    /** loads the data of a model with [id] */
    suspend fun getModel(id: UUID): Response<ModelData>

    /** flags a brand with [id] as deleted */
    suspend fun deleteModel(id: UUID): Response<Unit>

    /** restores a deleted model with [id] */
    suspend fun restoreModel(id: UUID): Response<Unit>

}