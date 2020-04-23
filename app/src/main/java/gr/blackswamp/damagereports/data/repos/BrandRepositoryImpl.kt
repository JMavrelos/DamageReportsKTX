package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.data.Response
import gr.blackswamp.core.util.toThrowable
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.logic.model.BrandData
import org.koin.core.inject
import java.util.*

class BrandRepositoryImpl : BaseRepositoryImpl(), BrandRepository {
    private val db: AppDatabase by inject()

    override fun getBrands(filter: String): Response<DataSource.Factory<Int, BrandData>> {
        return try {
            Response.success(db.brandDao.loadBrands(filter).map(BrandEntity::toData))
        } catch (t: Throwable) {
            Response.failure(t)
        }
    }

    override suspend fun getBrand(id: UUID): Response<BrandData> {
        return try {
            val brand =
                db.brandDao.loadBrandById(id)?.toData()
                    ?: throw getString(R.string.error_brand_not_found, id).toThrowable()
            Response.success(brand)
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