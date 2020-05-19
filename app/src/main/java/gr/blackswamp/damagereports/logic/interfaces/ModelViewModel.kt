package gr.blackswamp.damagereports.logic.interfaces

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import gr.blackswamp.damagereports.logic.commands.ModelCommand
import gr.blackswamp.damagereports.ui.model.Model
import java.util.*

interface ModelViewModel {
    val command: LiveData<ModelCommand>
    val modelList: LiveData<PagedList<Model>>
    val model: LiveData<Model>
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