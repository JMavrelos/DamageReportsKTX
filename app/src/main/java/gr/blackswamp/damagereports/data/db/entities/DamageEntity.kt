package gr.blackswamp.damagereports.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "damages"
    , indices = [Index("report")]
    , foreignKeys = [ForeignKey(entity = ReportEntity::class, parentColumns = ["id"], childColumns = ["report"], onDelete = ForeignKey.CASCADE)]
)
data class DamageEntity(
    @PrimaryKey val id: UUID
    , val name: String
    , val description: String
    , val report: UUID
    , val created: Date = Date(System.currentTimeMillis())
)