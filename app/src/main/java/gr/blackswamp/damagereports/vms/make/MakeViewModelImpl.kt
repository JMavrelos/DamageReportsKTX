package gr.blackswamp.damagereports.vms.make

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.lifecycle.call
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.repos.MakeRepository
import gr.blackswamp.damagereports.data.toData
import gr.blackswamp.damagereports.ui.model.Brand
import gr.blackswamp.damagereports.ui.model.Model
import gr.blackswamp.damagereports.vms.BrandData
import gr.blackswamp.damagereports.vms.ModelData
import gr.blackswamp.damagereports.vms.base.BaseViewModel
import gr.blackswamp.damagereports.vms.make.viewmodels.BrandParent
import gr.blackswamp.damagereports.vms.make.viewmodels.MakeViewModel
import gr.blackswamp.damagereports.vms.make.viewmodels.ModelParent
import org.koin.core.inject
import timber.log.Timber
import java.util.*

class MakeViewModelImpl(application: Application, val brandId: UUID?, runInit: Boolean = true) :
    BaseViewModel(application)
    , MakeViewModel
    , BrandParent
    , ModelParent {

    private val repo: MakeRepository by inject()
    private val dispatchers: IDispatchers by inject()

    companion object {
        const val TAG = "MakeViewModel"
        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 100
    }

    //region live data
    override val themeSetting: LiveData<ThemeSetting> = repo.themeSettingLive
    internal val brandFilter = MutableLiveData<String>()
    override val brand = MutableLiveData<Brand>()
    override val brandList: LiveData<PagedList<Brand>> = brandFilter.switchMap(this::brandDbToUi)
    override val model = MutableLiveData<Model>()
    //endregion

    init {
        Timber.d("Brand id $brandId")
        if (runInit) initialize()
    }

    @VisibleForTesting
    internal fun initialize() {
        brandFilter.postValue("")
    }

    //region make activity view model
    override val error = LiveEvent<String>()

    //endregion
    //region brand parent
    override fun newBrandFilter(filter: String, submitted: Boolean): Boolean {
        if (brandId != null) return true//if we do not have a pre set brand then we change the filter
        brandFilter.postValue(filter)
        if (submitted)
            hideKeyboard.call()
        return true
    }

    override fun editBrand(id: UUID) {
        brand.postValue(BrandData(id, "test brand"))
    }

    override fun saveBrand(name: String) {
        brand.postValue(null)
    }

    override fun newBrand() {
        brand.postValue(BrandData(EmptyUUID, ""))
    }

    override fun cancelBrand() {
        brand.postValue(null)
    }


    private fun brandDbToUi(filter: String): LiveData<PagedList<Brand>> {
        return repo.getBrands(filter, brandId).let { response ->
            if (response.hasError) {
                error.postValue(response.errorMessage)
                StaticDataSource.factory(listOf<Brand>())
            } else {
                response.get.map { it.toData() as Brand }
            }.toLiveData(LIST_PAGE_SIZE)
        }
    }
    //endregion

    //region model parent
    override fun editModel(id: UUID) {
        model.postValue(ModelData(id, "test model", EmptyUUID))
    }

    override fun saveModel(name: String) {
        model.postValue(null)
    }

    override fun newModel() {
        model.postValue(ModelData(EmptyUUID, "", EmptyUUID))
    }

    override fun cancelModel() {
        model.postValue(null)
    }
    //endregion
}