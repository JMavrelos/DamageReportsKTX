package gr.blackswamp.damagereports.ui.model

import android.os.Parcelable
import java.util.*

interface ReportHeader : Parcelable {
    val id: UUID
    val name: String
    val description: String
    val date: Date
}