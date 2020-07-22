package gr.blackswamp.damagereports.data.db.entities

import androidx.room.ColumnInfo
import java.math.BigDecimal
import java.util.*

data class ReportDamageEntity(
    @ColumnInfo(name = "id") val id: UUID
    , @ColumnInfo(name = "name") val name: String
    , @ColumnInfo(name = "pictures") val pictures: Int
    , @ColumnInfo(name = "parts") val parts: Int
    , @ColumnInfo(name = "cost") val cost: BigDecimal
)