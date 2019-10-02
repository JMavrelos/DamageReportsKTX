package gr.blackswamp.damagereports.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "damage_parts"
    , indices = [
        Index(value = ["part"])
        , Index(value = ["damage"])
        , Index(value = ["created"])
    ]
    , foreignKeys = [
        ForeignKey(entity = PartEntity::class, parentColumns = ["id"], childColumns = ["part"], onDelete = ForeignKey.RESTRICT)
        , ForeignKey(entity = DamageEntity::class, parentColumns = ["id"], childColumns = ["damage"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DamagePartEntity(
    @PrimaryKey val id: UUID
    , val part: UUID
    , val damage: UUID
    , val created: Date
    , val quantity: Int

)
