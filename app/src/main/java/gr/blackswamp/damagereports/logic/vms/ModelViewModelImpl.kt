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
import gr.blackswamp.core.lifecycle.LiveEvent
import gr.blackswamp.core.util.EmptyUUID
import gr.blackswamp.core.util.refresh
import gr.blackswamp.core.util.toThrowable
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.data.repos.ModelRepository
import gr.blackswamp.damagereports.logic.commands.ModelCommand
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.interfaces.ModelViewModel
import gr.blackswamp.damagereports.logic.model.ModelData
import gr.blackswamp.damagereports.ui.model.Model
import kotlinx.coroutines.launch
import org.koin.core.inject
import timber.log.Timber
import java.util.*

class ModelViewModelImpl(application: Application, parent: FragmentParent, private val brandId: UUID, runInit: Boolean = true) :
    BaseViewModel(application, parent), ModelViewModel {
    companion object {
        const val TAG = "ModelViewModel"

        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 100
    }

    private val repo: ModelRepository by inject()

    //region live data
    override val command = LiveEvent<ModelCommand>()

    @VisibleForTesting
    internal val lastDeleted = MutableLiveData<UUID>()
    override val showUndo: LiveData<Boolean> = Transformations.map(lastDeleted) { it != null }

    @VisibleForTesting
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

    override fun refresh() = modelFilter.refresh()

    override fun create() {
        modelData.postValue(ModelData(EmptyUUID, "", EmptyUUID))
    }

    override fun edit(id: UUID) {
        showLoading(true)
        launch {
            try {
                val brand = repo.getModel(id).getOrThrow
                modelData.postValue(brand)
            } catch (t: Throwable) {
                showError(t.message ?: getString(R.string.error_loading_model, id))
            } finally {
                showLoading(false)
            }

        }
    }

    override fun save(name: String) {
        launch {
            showLoading(true)
            try {
                val current = modelData.value ?: throw getString(R.string.error_new_model_not_found).toThrowable()
                if (name.isBlank()) throw getString(R.string.error_empty_model_name).toThrowable()

                if (current.id == EmptyUUID) {
                    repo.newModel(name, brandId)
                } else {
                    repo.updateModel(current.id, brandId, name)
                }.getOrThrow(getString(R.string.error_saving_model))
                modelData.postValue(null)
                hideKeyboard()
            } catch (t: Throwable) {
                showError(t.message ?: getString(R.string.error_saving_model))
            } finally {
                showLoading(false)
            }
        }
    }

    override fun cancel() {
        modelData.postValue(null)
    }

    override fun delete(id: UUID) {
        showLoading(true)
        launch {
            try {
                repo.deleteModel(id).getOrThrow
                lastDeleted.postValue(id)
            } catch (t: Throwable) {
                showError(t.message ?: getString(R.string.error_deleting, id.toString()))
            } finally {
                showLoading(false)
            }
        }
    }

    override fun undoLastDelete() {
        showLoading(true)
        launch {
            val id = lastDeleted.value
            try {
                if (id == null)
                    throw getString(R.string.error_un_deleting_no_saved_value).toThrowable()
                repo.restoreModel(id).getOrThrow
                lastDeleted.postValue(null)
            } catch (t: Throwable) {
                showError(t.message ?: getString(R.string.error_un_deleting, id))
            } finally {
                showLoading(false)
            }
        }
    }

    override fun select(id: UUID) {
        launch {
            showLoading(true)
            try {
                val brand = repo.getModel(id).getOrThrow
                command.postValue(ModelCommand.ModelSelected(brand))
            } catch (t: Throwable) {
                showError(t.message ?: getString(R.string.error_loading_brand, id))
            } finally {
                showLoading(false)
            }
        }
    }

    private fun modelDbToUi(filter: String): LiveData<PagedList<Model>> {
        return repo.getModels(brandId, filter).let { response ->
            if (response.hasError) {
                showError(response.errorMessage)
                StaticDataSource.factory(listOf<Model>())
            } else {
                response.get.map { it as Model }
            }
        }.toLiveData(BrandViewModelImpl.LIST_PAGE_SIZE)
    }
    //endregion
}