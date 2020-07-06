package gr.blackswamp.damagereports.app

import androidx.room.Room
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.coroutines.DispatcherImpl
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.AppDatabaseImpl
import gr.blackswamp.damagereports.data.prefs.Preferences
import gr.blackswamp.damagereports.data.prefs.PreferencesImpl
import gr.blackswamp.damagereports.data.repos.*
import gr.blackswamp.damagereports.logic.vms.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {
    single<Dispatcher> { DispatcherImpl }
    single<Preferences> { PreferencesImpl(androidApplication()) }
    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabaseImpl::class.java, AppDatabaseImpl.DATABASE)
            .build()
    }
    single { IdlingResource() }
    //region repositories
    single<MainRepository> { MainRepositoryImpl() }
    single<ReportListRepository> { ReportListRepositoryImpl() }
    single<ReportViewRepository> { ReportViewRepositoryImpl() }
    single<BrandRepository> { BrandRepositoryImpl() }
    single<ModelRepository> { ModelRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    //endregion

    //region view models
    viewModel { MainViewModelImpl(androidApplication()) }
    viewModel { paramList -> ReportListViewModelImpl(androidApplication(), paramList[0], true) }
    viewModel { paramList -> ReportViewViewModelImpl(androidApplication(), paramList[0], paramList[1], paramList[2], true) }
    viewModel { paramList -> BrandViewModelImpl(androidApplication(), paramList[0], true) }
    viewModel { paramList -> ModelViewModelImpl(androidApplication(), paramList[0], paramList[1], true) }
    //endregion
}