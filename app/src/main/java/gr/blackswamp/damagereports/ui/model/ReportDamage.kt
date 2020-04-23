package gr.blackswamp.damagereports.ui.model

import android.os.Parcelable

interface ReportDamage : Parcelable {
    val name: String
    val pictures: Int
    val parts: Int
    val cost: Double
}