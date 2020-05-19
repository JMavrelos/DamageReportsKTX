package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.data.Response
import gr.blackswamp.core.util.toThrowable
import gr.blackswamp.damagereports.R
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

    override suspend fun getModel(id: UUID): Response<ModelData> {
        return try {
            val model =
                db.modelDao.loadModelById(id)?.toData()
                    ?: throw getString(R.string.error_model_not_found, id).toThrowable()
            Response.success(model)
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override suspend fun newModel(name: String, brandId: UUID): Response<Unit> {
        val model = ModelEntity(UUID.randomUUID(), name, brandId, false)
        return try {
            db.modelDao.insertModel(model)
            Response.success()
        } catch (t: Throwable) {
            Response.failure("Model entity with name $name already exists", t)
        }

    }

    override suspend fun updateModel(id: UUID, brandId: UUID, name: String): Response<Unit> {
        val model = ModelEntity(id, name, brandId, false)
        return try {
            val affected = db.modelDao.updateModel(model)
            if (affected == 0) {
                Response.failure("Model entity $id could not be found")
            } else {
                Response.success()
            }
        } catch (t: Throwable) {
            Response.failure("Model entity with $name already exists", t)
        }
    }

    override suspend fun deleteModel(id: UUID): Response<Unit> {
        return try {
            val affected = db.modelDao.flagModelDeleted(id)
            if (affected == 0)
                return Response.failure(getString(R.string.error_model_not_found, id))
            Response.success()
        } catch (t: Throwable) {
            return Response.failure(getString(R.string.error_deleting, (t.message ?: t::class.java.name)), t)
        }
    }

    override suspend fun restoreModel(id: UUID): Response<Unit> {
        return try {
            val affected = db.modelDao.unFlagModelDeleted(id)
            if (affected == 0)
                return Response.failure(getString(R.string.error_no_deleted_model, id))
            Response.success()
        } catch (t: Throwable) {
            return Response.failure(getString(R.string.error_un_deleting, (t.message ?: t::class.java.name)), t)
        }
    }
}