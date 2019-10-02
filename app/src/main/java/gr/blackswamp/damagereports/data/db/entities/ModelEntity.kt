package gr.blackswamp.damagereports.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "models"
    , indices = [
        Index(value = ["name"])
        , Index(value = ["brand"])
        , Index(value = ["deleted"])
    ]
    , foreignKeys = [ForeignKey(entity = BrandEntity::class, parentColumns = ["id"], childColumns = ["brand"], onDelete = ForeignKey.CASCADE)]
)

data class ModelEntity(
    @PrimaryKey val id: UUID
    , val name: String
    , val brand: UUID
    , val deleted: Boolean
)