package gr.blackswamp.damagereports.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gr.blackswamp.damagereports.data.db.converters.DateConverter
import gr.blackswamp.damagereports.data.db.converters.UUIDConverter
import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.dao.DamageDao
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.db.entities.*


@Database(
    entities = [BrandEntity::class, DamageEntity::class, DamagePartEntity::class, ModelEntity::class, PartEntity::class, ReportEntity::class],
    version = 1
)
@TypeConverters(DateConverter::class, UUIDConverter::class)
abstract class AppDatabase : RoomDatabase(), IDatabase {
    companion object {
        const val DATABASE = "data.db"
    }

    abstract override val reportDao: ReportDao
    abstract override val modelDao: ModelDao
    abstract override val brandDao: BrandDao
    abstract override val damageDao: DamageDao


}