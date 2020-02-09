package gr.blackswamp.damagereports.data.repos

import androidx.paging.DataSource
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.data.Response
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import org.koin.core.inject
import java.util.*

class MakeRepositoryImpl : BaseRepositoryImpl(), MakeRepository {
    private val db: AppDatabase by inject()
    private val dispatchers: IDispatchers by inject()

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


}