package gr.blackswamp.damagereports.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "parts"
    , indices = [
        Index(value = ["name"])
        , Index(value = ["brand"])
        , Index(value = ["model"])
        , Index(value = ["brand", "model"])
        , Index(value = ["deleted"])
    ]
, foreignKeys = [
        ForeignKey(entity = BrandEntity::class, parentColumns = ["id"], childColumns = ["brand"], onDelete = ForeignKey.RESTRICT)
        , ForeignKey(entity = ModelEntity::class, parentColumns = ["id"], childColumns = ["model"], onDelete = ForeignKey.RESTRICT)
    ]
)
data class PartEntity(
    @PrimaryKey val id: UUID
    , val name: String
    , val brand: UUID?
    , val model: UUID?
    , val price: BigDecimal
    , val deleted: Boolean
)
