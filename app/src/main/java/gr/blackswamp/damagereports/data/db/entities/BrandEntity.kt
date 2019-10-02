package gr.blackswamp.damagereports.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "brands"
    , indices = [Index(value = ["name"]), Index(value = ["deleted"])])

data class BrandEntity(
    @PrimaryKey val id: UUID
    , val name: String
    , val deleted: Boolean
)