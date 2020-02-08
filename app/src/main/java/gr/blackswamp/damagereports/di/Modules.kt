package gr.blackswamp.damagereports.di

import androidx.room.Room
import gr.blackswamp.core.coroutines.AppDispatchers
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.AppDatabaseImpl
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.data.prefs.PreferencesImpl
import gr.blackswamp.damagereports.data.repos.*
import gr.blackswamp.damagereports.vms.make.MakeViewModelImpl
import gr.blackswamp.damagereports.vms.reports.ReportViewModelImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {
    single<IDispatchers> { AppDispatchers }
    single<Preferences> { PreferencesImpl(androidApplication()) }
    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabaseImpl::class.java, AppDatabaseImpl.DATABASE)
            .build()
    }

    single<ReportRepository> { ReportRepositoryImpl() }

    single<MakeRepository> { MakeRepositoryImpl() }

    single<SettingsRepository> { SettingsRepositoryImpl() }

    viewModel { ReportViewModelImpl(androidApplication(), true) }

    viewModel { paramList -> MakeViewModelImpl(androidApplication(), paramList[0]) }
}