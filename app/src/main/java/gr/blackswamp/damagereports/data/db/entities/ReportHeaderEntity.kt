package gr.blackswamp.damagereports.data.db.entities

import androidx.room.ColumnInfo
import gr.blackswamp.damagereports.reports.model.ReportHeader
import java.util.*

data class ReportHeaderEntity(
    @ColumnInfo(name = "id") override val id: UUID
    , @ColumnInfo(name = "name") override val name: String
    , @ColumnInfo(name = "description") override val description: String
    , @ColumnInfo(name = "created") override val date: Date)
    : ReportHeader