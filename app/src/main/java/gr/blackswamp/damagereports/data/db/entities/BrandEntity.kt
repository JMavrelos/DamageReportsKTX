package gr.blackswamp.damagereports.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.NOCASE
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "brands"
    , indices = [Index(value = ["name"], unique = true), Index(value = ["deleted"])]
)

data class BrandEntity(
    @PrimaryKey val id: UUID
    , @ColumnInfo(collate = NOCASE) val name: String
    , val deleted: Boolean
)