package gr.blackswamp.damagereports.ui.model

import android.os.Parcelable
import java.util.*

interface Model : Parcelable {
    val id: UUID
    val name: String
    val brand: UUID
}