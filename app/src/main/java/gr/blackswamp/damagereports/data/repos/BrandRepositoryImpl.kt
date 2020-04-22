package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import org.koin.core.inject
import java.util.*

class BrandRepositoryImpl : BaseRepositoryImpl(), BrandRepository {
    private val db: AppDatabase by inject()
    private val dispatchers: Dispatcher by inject()

    override fun getBrands(filter: String, withId: UUID?): Response<DataSource.Factory<Int, BrandEntity>> {
        return try {
            Response.success(
                if (withId == null) {
                    db.brandDao.loadBrands(filter)
                } else {
                    db.brandDao.loadBrandFactoryById(withId)
                }
            )
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override suspend fun newBrand(name: String): Response<Unit> {
        val brand = BrandEntity(UUID.randomUUID(), name, false)
        return try {
            db.brandDao.insertBrand(brand)
            Response.success()
        } catch (t: Throwable) {
            Response.failure("Brand entity with name $name already exists", t)
        }

    }

    override suspend fun updateBrand(id: UUID, name: String): Response<Unit> {
        val brand = BrandEntity(id, name, false)
        return try {
            val affected = db.brandDao.updateBrand(brand)
            if (affected == 0) {
                Response.failure("Brand entity $id could not be found")
            } else {
                Response.success()
            }
        } catch (t: Throwable) {
            Response.failure("Brand entity with $name already exists", t)
        }
    }
}