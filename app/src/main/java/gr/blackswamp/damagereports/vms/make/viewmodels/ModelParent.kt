package gr.blackswamp.damagereports.vms.make.viewmodels

import androidx.lifecycle.LiveData
import gr.blackswamp.damagereports.ui.model.Model
import java.util.*

interface ModelParent {
    val model: LiveData<Model>

    fun editModel(id: UUID)
    fun saveModel(name: String)
    fun newModel()
    fun cancelModel()
}