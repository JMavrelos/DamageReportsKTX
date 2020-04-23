package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.logic.commands.BrandCommand
import gr.blackswamp.damagereports.ui.model.Brand
import java.util.*

interface BrandViewModel {
    val command: LiveData<BrandCommand>
    val brandList: LiveData<PagedList<Brand>>
    val brand: LiveData<Brand>

    fun newFilter(filter: String, submitted: Boolean): Boolean
    fun create()
    fun edit(id: UUID)
    fun save(name: String)
    fun delete(id: UUID)
    fun select(id: UUID)
    fun cancel()
}