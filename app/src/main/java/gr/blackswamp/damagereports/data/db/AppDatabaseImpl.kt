package gr.blackswamp.damagereports.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gr.blackswamp.core.db.converters.BigDecimalConverter
import gr.blackswamp.damagereports.data.db.converters.StringDateConverter
import gr.blackswamp.damagereports.data.db.converters.UUIDConverter
import gr.blackswamp.damagereports.data.db.dao.*
import gr.blackswamp.damagereports.data.db.entities.*


@Database(
    entities = [BrandEntity::class, DamageEntity::class, DamagePartEntity::class, ModelEntity::class, PartEntity::class, ReportEntity::class],
    version = 1
)
@TypeConverters(StringDateConverter::class, UUIDConverter::class, BigDecimalConverter::class)
abstract class AppDatabaseImpl : RoomDatabase(), AppDatabase {
    companion object {
        const val DATABASE = "data.db"
    }

    abstract override val globalDao: GlobalDao
    abstract override val reportDao: ReportDao
    abstract override val damageDao: DamageDao
    abstract override val damagePartDao: DamagePartDao
    abstract override val brandDao: BrandDao
    abstract override val modelDao: ModelDao
    abstract override val partDao: PartDao
}