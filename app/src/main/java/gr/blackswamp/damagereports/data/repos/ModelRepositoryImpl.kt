package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.entities.ModelEntity
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.logic.model.ModelData
import org.koin.core.inject
import java.util.*

class ModelRepositoryImpl : BaseRepositoryImpl(), ModelRepository {
    private val db: AppDatabase by inject()
    private val dispatchers: Dispatcher by inject()

    override fun getModels(parent: UUID, filter: String): Response<DataSource.Factory<Int, ModelData>> {
        return try {
            Response.success(db.modelDao.loadModels(parent, filter).map(ModelEntity::toData))
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override suspend fun newModel(name: String, brandId: UUID): Response<Unit> {
        val Model = ModelEntity(UUID.randomUUID(), name, brandId, false)
        return try {
            db.modelDao.insertModel(Model)
            Response.success()
        } catch (t: Throwable) {
            Response.failure("Model entity with name $name already exists", t)
        }

    }

    override suspend fun updateModel(id: UUID, brandId: UUID, name: String): Response<Unit> {
        val Model = ModelEntity(id, name, brandId, false)
        return try {
            val affected = db.modelDao.updateModel(Model)
            if (affected == 0) {
                Response.failure("Model entity $id could not be found")
            } else {
                Response.success()
            }
        } catch (t: Throwable) {
            Response.failure("Model entity with $name already exists", t)
        }
    }
}