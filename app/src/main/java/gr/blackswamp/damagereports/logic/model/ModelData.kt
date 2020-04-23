package gr.blackswamp.damagereports.logic.model

import gr.blackswamp.damagereports.ui.model.Model
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ModelData(override val id: UUID, override val name: String, override val brand: UUID) : Model

//data class PartData(val id: UUID, val name: String, val brand: UUID?, val model: UUID?, val price: Double)

//@Parcelize
//data class DamageData(override val name: String, override val pictures: Int, override val parts: Int, override val cost: Double) : ReportDamage