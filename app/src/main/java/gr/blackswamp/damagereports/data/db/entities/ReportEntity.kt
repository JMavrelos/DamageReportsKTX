package gr.blackswamp.damagereports.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reports"
    , indices = [
        Index(value = ["name"])
        , Index(value = ["description"])
        , Index(value = ["created"])
        , Index(value = ["brand"])
        , Index(value = ["model"])
    ]
    //temporarily disabled
//    , foreignKeys = [
//        ForeignKey(entity = BrandEntity::class, parentColumns = ["id"], childColumns = ["brand"], onDelete = ForeignKey.RESTRICT)
//        , ForeignKey(entity = ModelEntity::class, parentColumns = ["id"], childColumns = ["model"], onDelete = ForeignKey.RESTRICT)
//    ]
)
data class ReportEntity(
    @PrimaryKey val id: UUID
    , val name: String
    , val description: String
    , val brand: UUID
    , val model: UUID
    , val created: Date = Date(System.currentTimeMillis())
    , val updated: Date = Date(System.currentTimeMillis()))