package gr.blackswamp.damagereports.ui.model

import android.os.Parcelable
import java.math.BigDecimal
import java.util.*

interface ReportDamage : Parcelable {
    val id: UUID
    val name: String
    val pictures: Int
    val parts: Int
    val cost: BigDecimal
}