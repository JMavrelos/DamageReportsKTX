package gr.blackswamp.damagereports.logic.vms

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.vms.CoreViewModel
import gr.blackswamp.damagereports.data.repos.BrandRepository
import gr.blackswamp.damagereports.logic.model.BrandData
import gr.blackswamp.damagereports.logic.model.ModelData
import gr.blackswamp.damagereports.ui.model.Model
import org.koin.core.inject
import timber.log.Timber
import java.util.*

class ModelViewModelImpl(application: Application, val brandId: UUID?, runInit: Boolean = true) :
    CoreViewModel(application), ModelViewModel {

    private val repo: BrandRepository by inject()

    companion object {
        const val TAG = "ModelViewModel"

        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 100
    }

    //region live data
    internal val brandFilter = MutableLiveData<String>()
    private val brandData = MutableLiveData<BrandData>()
    private val modelData = MutableLiveData<ModelData>()
    override val model: LiveData<Model> = Transformations.map(modelData) { it }
    //endregion

    init {
        Timber.d("Brand id $brandId")
        if (runInit) initialize()
    }

    @VisibleForTesting
    internal fun initialize() {
        brandFilter.postValue("")
    }

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