package gr.blackswamp.damagereports.data.repos

import gr.blackswamp.damagereports.data.db.entities.BrandEntity
import gr.blackswamp.damagereports.data.db.entities.ModelEntity
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import gr.blackswamp.damagereports.vms.BrandData
import gr.blackswamp.damagereports.vms.ModelData
import gr.blackswamp.damagereports.vms.ReportData
import gr.blackswamp.damagereports.vms.ReportHeaderData

internal fun ReportHeaderEntity.toData(): ReportHeaderData =
    ReportHeaderData(id, name, description, date)

internal fun ReportEntity.toData(brand: BrandEntity, model: ModelEntity): ReportData =
    ReportData(this.id, this.name, this.description, brand.toData(), model.toData(), this.created)

private fun ModelEntity.toData(): ModelData =
    ModelData(this.id, this.name, this.brand)

private fun BrandEntity.toData(): BrandData =
    BrandData(this.id, this.name)

