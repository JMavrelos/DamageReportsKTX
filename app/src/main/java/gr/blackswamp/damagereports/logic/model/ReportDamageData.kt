package gr.blackswamp.damagereports.logic.model

import gr.blackswamp.damagereports.ui.model.ReportDamage
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
class ReportDamageData(override val id: UUID, override val name: String, override val pictures: Int, override val parts: Int, override val cost: BigDecimal) : ReportDamage