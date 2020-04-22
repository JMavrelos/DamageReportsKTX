package gr.blackswamp.damagereports.logic.vms

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.ui.model.Model
import java.util.*

interface ModelViewModel {
    val model: LiveData<Model>

    fun editModel(id: UUID)
    fun saveModel(name: String)
    fun newModel()
    fun cancelModel()
}