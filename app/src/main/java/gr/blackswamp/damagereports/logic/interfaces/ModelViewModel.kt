package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.ui.model.Model
import java.util.*

interface ModelViewModel {
    val model: LiveData<Model>
    val modelList: LiveData<PagedList<Model>>

    fun newFilter(filter: String, submitted: Boolean): Boolean
    fun newModel()
    fun edit(id: UUID)
    fun save(name: String)
    fun cancel()
}