package gr.blackswamp.damagereports.data

import gr.blackswamp.damagereports.data.db.entities.*
import gr.blackswamp.damagereports.logic.model.*

internal fun ReportHeaderEntity.toData(): ReportHeaderData =
    ReportHeaderData(id, name, description, date)

internal fun ReportEntity.toData(brand: BrandEntity, model: ModelEntity): ReportData =
    ReportData(this.id, this.name, this.description, brand.toData(), model.toData(), this.created)

internal fun ModelEntity.toData(): ModelData =
    ModelData(this.id, this.name, this.brand)

internal fun BrandEntity.toData(): BrandData =
    BrandData(this.id, this.name)

internal fun ReportDamageEntity.toData() =
    ReportDamageData(id, name, pictures, parts, cost)