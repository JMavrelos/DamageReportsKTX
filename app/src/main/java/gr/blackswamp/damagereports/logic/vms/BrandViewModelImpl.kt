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
import gr.blackswamp.damagereports.data.repos.BrandRepository
import gr.blackswamp.damagereports.logic.commands.BrandCommand
import gr.blackswamp.damagereports.logic.interfaces.BrandViewModel
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.model.BrandData
import gr.blackswamp.damagereports.ui.model.Brand
import kotlinx.coroutines.launch
import org.koin.core.inject
import java.util.*

class BrandViewModelImpl(application: Application, parent: FragmentParent, runInit: Boolean = true) :
    BaseViewModel(application, parent), BrandViewModel {
    companion object {
        const val TAG = "MakeViewModel"

        @VisibleForTesting
        internal const val LIST_PAGE_SIZE = 100
    }

    private val repo: BrandRepository by inject()

    //region live data
    override val command = LiveEvent<BrandCommand>()

    @VisibleForTesting
    internal val lastDeleted = MutableLiveData<UUID>()
    override val showUndo: LiveData<Boolean> = Transformations.map(lastDeleted) { it != null }

    @VisibleForTesting
    internal val brandFilter = MutableLiveData<String>()

    @VisibleForTesting
    internal val brandData = MutableLiveData<BrandData>()
    override val brand: LiveData<Brand> = Transformations.map(brandData) { it }
    override val brandList: LiveData<PagedList<Brand>> = brandFilter.switchMap(this::brandDbToUi)
    //endregion

    init {
        if (runInit) initialize()
    }

    @VisibleForTesting
    internal fun initialize() {
        brandFilter.postValue("")
    }

    override fun newFilter(filter: String, submitted: Boolean): Boolean {
        brandFilter.postValue(filter)
        if (submitted)
            hideKeyboard()
        return true
    }

    override fun refresh() = brandFilter.refresh()

    override fun create() {
        brandData.postValue(BrandData(EmptyUUID, ""))
    }

    override fun edit(id: UUID) {
        showLoading(true)
        launch {
            try {
                val brand = repo.getBrand(id).getOrThrow
                brandData.postValue(brand)
            } catch (t: Throwable) {
                showError(t.message ?: getString(R.string.error_loading_brand, id))
            } finally {
                showLoading(false)
            }
        }
    }

    override fun save(name: String) {
        showLoading(true)
        launch {
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
                showError(t.message ?: getString(R.string.error_saving_brand))
            } finally {
                showLoading(false)
            }
        }
    }

    override fun cancel() {
        brandData.postValue(null)
    }

    override fun delete(id: UUID) {
        showLoading(true)
        launch {
            try {
                repo.deleteBrand(id).getOrThrow
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
                repo.restoreBrand(id).getOrThrow
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
                val brand = repo.getBrand(id).getOrThrow
                command.postValue(BrandCommand.ShowModelSelect(brand))
            } catch (t: Throwable) {
                showError(t.message ?: getString(R.string.error_loading_brand, id))
            } finally {
                showLoading(false)
            }
        }
    }

    private fun brandDbToUi(filter: String): LiveData<PagedList<Brand>> {
        return repo.getBrands(filter).let { response ->
            if (response.hasError) {
                showError(response.errorMessage)
                StaticDataSource.factory(listOf<Brand>())
            } else {
                response.get.map { it as Brand }
            }
        }.toLiveData(LIST_PAGE_SIZE)
    }
}