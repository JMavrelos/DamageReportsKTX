package gr.blackswamp.damagereports.data.repos

import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import gr.blackswamp.damagereports.vms.ReportHeaderData

internal fun ReportHeaderEntity.toData(): ReportHeaderData {
    return ReportHeaderData(id, name, description, date)
}
