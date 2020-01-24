package gr.blackswamp.damagereports.data.repos

import android.app.Application
import androidx.lifecycle.LiveData
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.prefs.Preferences
import org.koin.core.KoinComponent
import org.koin.core.inject

class ModelRepositoryImpl() : ModelRepository, KoinComponent {
    private val db: AppDatabase by inject()
    private val prefs: Preferences by inject()
    private val dispatchers: IDispatchers by inject()
    private val application: Application by inject()

    override val darkThemeLive: LiveData<Boolean> = prefs.darkThemeLive

}