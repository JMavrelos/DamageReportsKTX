package gr.blackswamp.damagereports.data.db.entities

import androidx.room.ColumnInfo
import java.util.*

data class ReportHeaderEntity(
    @ColumnInfo(name = "id") val id: UUID
    , @ColumnInfo(name = "name") val name: String
    , @ColumnInfo(name = "description") val description: String
    , @ColumnInfo(name = "created") val date: Date
)
