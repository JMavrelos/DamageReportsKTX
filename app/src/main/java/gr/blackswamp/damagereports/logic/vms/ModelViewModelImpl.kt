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
import gr.blackswamp.damagereports.data.repos.ModelRepository
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ModelViewModel
import gr.blackswamp.damagereports.logic.model.ModelData
import gr.blackswamp.damagereports.ui.model.Model
import kotlinx.coroutines.launch
import org.koin.core.inject
import timber.log.Timber
import java.util.*

class ModelViewModelImpl(application: Application, val parent: FragmentParent, val brandId: UUID, runInit: Boolean = true) :
    CoreViewModel(application), ModelViewModel {
    companion object {
        const val TAG = "ModelViewModel"

        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 100
    }

    private val repo: ModelRepository by inject()

    //region live data
    internal val modelFilter = MutableLiveData<String>()
    private val modelData = MutableLiveData<ModelData>()
    override val model: LiveData<Model> = Transformations.map(modelData) { it }
    override val modelList: LiveData<PagedList<Model>> = modelFilter.switchMap(this::modelDbToUi)
    //endregion

    init {
        Timber.d("Brand id $brandId")
        if (runInit) initialize()
    }

    @VisibleForTesting
    internal fun initialize() {
        modelFilter.postValue("")
    }

    override fun newFilter(filter: String, submitted: Boolean): Boolean {
        modelFilter.postValue(filter)
        if (submitted)
            parent.hideKeyboard()
        return true
    }

    override fun edit(id: UUID) {
        modelData.postValue(ModelData(id, "test model", EmptyUUID))
    }

    override fun save(name: String) {
        launch {
            parent.showLoading(true)
            try {
                val current = modelData.value ?: throw getString(R.string.error_new_model_not_found).toThrowable()
                if (name.isBlank()) throw getString(R.string.error_empty_model_name).toThrowable()

                if (current.id == EmptyUUID) {
                    repo.newModel(name, brandId)
                } else {
                    repo.updateModel(current.id, brandId, name)
                }.getOrThrow(getString(R.string.error_saving_brand))
                modelData.postValue(null)
            } catch (t: Throwable) {
                parent.showError(t.message ?: getString(R.string.error_saving_brand))
            } finally {
                parent.showLoading(false)
            }
        }
    }

    override fun newModel() {
        modelData.postValue(ModelData(EmptyUUID, "", EmptyUUID))
    }

    override fun cancel() {
        modelData.postValue(null)
    }

    private fun modelDbToUi(filter: String): LiveData<PagedList<Model>> {
        return repo.getModels(brandId, filter).let { response ->
            if (response.hasError) {
                parent.showError(response.errorMessage)
                StaticDataSource.factory(listOf<Model>())
            } else {
                response.get.map { it as Model }
            }
        }.toLiveData(BrandViewModelImpl.LIST_PAGE_SIZE)
    }
    //endregion
}