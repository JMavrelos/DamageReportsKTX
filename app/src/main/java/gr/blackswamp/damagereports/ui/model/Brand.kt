package gr.blackswamp.damagereports.ui.model

import android.os.Parcelable
import java.util.*

interface Brand : Parcelable {
    val id: UUID
    val name: String
}