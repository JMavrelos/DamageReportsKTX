package gr.blackswamp.damagereports.logic.model

import gr.blackswamp.damagereports.ui.model.ReportHeader
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ReportHeaderData(override val id: UUID, override val name: String, override val description: String, override val date: Date) :
    ReportHeader