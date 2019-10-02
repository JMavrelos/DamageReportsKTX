package gr.blackswamp.damagereports.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gr.blackswamp.core.schedulers.AppDispatchers
import gr.blackswamp.damagereports.App
import gr.blackswamp.damagereports.data.db.AppDatabase

class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val database = AppDatabase.instance(App.context)
        val application = App.application
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(application, database, AppDispatchers) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

