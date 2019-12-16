package gr.blackswamp.damagereports.vms.reports.model

import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import gr.blackswamp.damagereports.ui.reports.model.ReportHeader
import java.util.*

data class ReportHeaderData(
    override val id: UUID
    , override val name: String
    , override val description: String
    , override val date: Date
) : ReportHeader {
    constructor(entity: ReportHeaderEntity) : this(
        entity.id
        , entity.name
        , entity.description
        , entity.date
    )
}