package gr.blackswamp.damagereports.vms.make

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.core.coroutines.IDispatchers
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import gr.blackswamp.damagereports.data.repos.MakeRepository
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

class MakeViewModelImpl(application: Application, val brandId: UUID?) :
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

    override val themeSetting: LiveData<ThemeSetting> = repo.themeSettingLive

    init {
        Timber.d(TAG, "Brand id $brandId")
    }

    //region brand parent
    override val brand = MutableLiveData<Brand>()

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
    //endregion

    //region model parent
    override val model = MutableLiveData<Model>()

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