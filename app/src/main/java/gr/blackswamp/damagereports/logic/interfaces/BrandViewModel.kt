package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.logic.commands.BrandCommand
import gr.blackswamp.damagereports.ui.model.Brand
import java.util.*

interface BrandViewModel {
    val command: LiveData<BrandCommand>
    val brandList: LiveData<PagedList<Brand>>
    val brand: LiveData<Brand>
    val refreshing: MutableLiveData<Boolean>
    val showUndo: LiveData<Boolean>

    fun newFilter(filter: String, submitted: Boolean): Boolean
    fun refresh()
    fun create()
    fun edit(id: UUID)
    fun save(name: String)
    fun cancel()
    fun delete(id: UUID)
    fun undoLastDelete()
    fun select(id: UUID)
}