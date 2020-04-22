package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.util.toThrowable
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.repos.BrandRepository
import gr.blackswamp.damagereports.logic.model.BrandData
import gr.blackswamp.damagereports.logic.model.ModelData
import gr.blackswamp.damagereports.ui.model.Brand
import kotlinx.coroutines.launch
import org.koin.core.inject
import timber.log.Timber
import java.util.*

class BrandViewModelImpl(application: Application, val brandId: UUID?, runInit: Boolean = true) :
    CoreViewModel(application), BrandViewModel {

    private val repo: BrandRepository by inject()

    companion object {
        const val TAG = "MakeViewModel"
        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 100
    }

    //region live data
    internal val brandFilter = MutableLiveData<String>()
    private val brandData = MutableLiveData<BrandData>()
    override val brand: LiveData<Brand> = Transformations.map(brandData) { it }
    override val brandList: LiveData<PagedList<Brand>> = brandFilter.switchMap(this::brandDbToUi)
    private val modelData = MutableLiveData<ModelData>()
    //endregion

    init {
        Timber.d("Brand id $brandId")
        if (runInit) initialize()
    }

    @VisibleForTesting
    internal fun initialize() {
        brandFilter.postValue("")
    }

    override fun newBrandFilter(filter: String, submitted: Boolean): Boolean {
        if (brandId != null) return true//if we do not have a pre set brand then we change the filter
        brandFilter.postValue(filter)
//        if (submitted)
//            hideKeyboard.call()
        return true
    }

    override fun editBrand(id: UUID) {
        brandData.postValue(BrandData(id, "test brand"))
    }

    override fun saveBrand(name: String) {
        launch {
//            loading.postValue(true)
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
//                error.postValue(t.message ?: getString(R.string.error_saving_brand))
            } finally {
//                loading.postValue(false)
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
//            if (response.hasError) {
//                error.postValue(response.errorMessage)
                StaticDataSource.factory(listOf<Brand>())
//            } else {
//                response.get.map { it.toData() }
//            }
                    .toLiveData(LIST_PAGE_SIZE)
        }
    }
}