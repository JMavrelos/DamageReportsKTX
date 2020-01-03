package gr.blackswamp.damagereports.vms

import gr.blackswamp.damagereports.ui.model.ReportHeader
import java.util.*

data class BrandData(val id: UUID, val name: String)

data class PartData(val id: UUID, val name: String, val brand: UUID?, val model: UUID?, val price: Double)

data class ReportData(val id: UUID, val name: String, val description: String, val brand: BrandData, val created: Date)

data class ReportHeaderData(override val id: UUID, override val name: String, override val description: String, override val date: Date) : ReportHeader