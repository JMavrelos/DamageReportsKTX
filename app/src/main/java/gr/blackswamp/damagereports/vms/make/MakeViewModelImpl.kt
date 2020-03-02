package gr.blackswamp.damagereports.vms.make

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.lifecycle.call
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.util.toThrowable
import gr.blackswamp.damagereports.R
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
import kotlinx.coroutines.launch
import org.koin.core.inject
import timber.log.Timber
import java.util.*

class MakeViewModelImpl(application: Application, val brandId: UUID?, runInit: Boolean = true) :
    BaseViewModel(application)
    , MakeViewModel
    , BrandParent
    , ModelParent {

    private val repo: MakeRepository by inject()

    companion object {
        const val TAG = "MakeViewModel"
        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 100
    }

    //region live data
    override val error = LiveEvent<String>()
    override val loading = MutableLiveData<Boolean>(false)
    override val themeSetting: LiveData<ThemeSetting> = repo.themeSettingLive
    internal val brandFilter = MutableLiveData<String>()
    private val brandData = MutableLiveData<BrandData>()
    override val brand: LiveData<Brand> = Transformations.map(brandData) { it as? Brand }
    override val brandList: LiveData<PagedList<Brand>> = brandFilter.switchMap(this::brandDbToUi)
    private val modelData = MutableLiveData<ModelData>()
    override val model: LiveData<Model> = Transformations.map(modelData) { it as? Model }
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
        brandData.postValue(BrandData(id, "test brand"))
    }

    override fun saveBrand(name: String) {
        launch {
            loading.postValue(true)
            try {
                val current = brandData.value ?: throw getString(R.string.error_new_brand_not_found).toThrowable()
                if (name.isBlank()) throw getString(R.string.error_empty_brand_name).toThrowable()

                if (current.id == EmptyUUID) {
                    repo.newBrand(name)
                } else {
                    repo.updateBrand(current.id, name)
                }.getOrThrow(getString(R.string.error_saving_brand))
                brandData.postValue(null)
            } catch (t: Throwable) {
                error.postValue(t.message ?: getString(R.string.error_saving_brand))
            } finally {
                loading.postValue(false)
            }
        }
    }

    override fun newBrand() {
        brandData.postValue(BrandData(EmptyUUID, ""))
    }

    override fun cancelBrand() {
        brandData.postValue(null)
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
        modelData.postValue(ModelData(id, "test model", EmptyUUID))
    }

    override fun saveModel(name: String) {
        modelData.postValue(null)
    }

    override fun newModel() {
        modelData.postValue(ModelData(EmptyUUID, "", EmptyUUID))
    }

    override fun cancelModel() {
        modelData.postValue(null)
    }
    //endregion
}