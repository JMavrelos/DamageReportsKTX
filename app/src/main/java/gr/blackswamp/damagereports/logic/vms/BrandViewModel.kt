package gr.blackswamp.damagereports.logic.vms

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.ui.model.Brand
import java.util.*

interface BrandViewModel {
    val brandList: LiveData<PagedList<Brand>>
    val brand: LiveData<Brand>

    fun editBrand(id: UUID)
    fun saveBrand(name: String)
    fun newBrand()
    fun cancelBrand()
    fun newBrandFilter(filter: String, submitted: Boolean): Boolean
}