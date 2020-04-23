package gr.blackswamp.damagereports.logic.model

import gr.blackswamp.damagereports.ui.model.Brand
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class BrandData(override val id: UUID, override val name: String) : Brand