package gr.blackswamp.damagereports.data.repos

import android.app.Application
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.damagereports.data.db.AppDatabase
import org.koin.core.KoinComponent
import org.koin.core.inject

class ModelRepositoryImpl() : BaseRepositoryImpl(), ModelRepository, KoinComponent {
    private val db: AppDatabase by inject()
    private val dispatchers: IDispatchers by inject()
    private val application: Application by inject()

}