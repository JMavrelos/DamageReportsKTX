package gr.blackswamp.damagereports.vms

import gr.blackswamp.damagereports.ui.model.Report
import gr.blackswamp.damagereports.ui.model.ReportHeader
import java.util.*

data class ReportData(
    override val id: UUID,
    override val name: String = "",
    override val description: String = "",
    val brand: BrandData?,
    val model: ModelData?,
    override val created: Date,
    override val changed: Boolean = false
) : Report {
    override val brandName: String? = brand?.name
    override val modelName: String? = model?.name
}

data class ReportHeaderData(override val id: UUID, override val name: String, override val description: String, override val date: Date) : ReportHeader

data class BrandData(val id: UUID, val name: String)

data class ModelData(val id: UUID, val name: String, val brand: UUID)

data class PartData(val id: UUID, val name: String, val brand: UUID?, val model: UUID?, val price: Double)
