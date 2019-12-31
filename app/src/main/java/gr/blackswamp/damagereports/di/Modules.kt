package gr.blackswamp.damagereports.di

import androidx.room.Room
import gr.blackswamp.core.coroutines.AppDispatchers
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.logging.AppLog
import gr.blackswamp.core.logging.ILog
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.IDatabase
import gr.blackswamp.damagereports.data.prefs.IPreferences
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.data.repos.IReportRepository
import gr.blackswamp.damagereports.data.repos.ReportRepository
import gr.blackswamp.damagereports.vms.reports.ReportViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {
    single<IDispatchers> { AppDispatchers }
    single<ILog> { AppLog }
    single<IPreferences> { Preferences(androidApplication()) }
    single<IDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DATABASE)
            .build()
    }

    single<IReportRepository> { ReportRepository() }

    viewModel { ReportViewModel(androidApplication(),true) }
}