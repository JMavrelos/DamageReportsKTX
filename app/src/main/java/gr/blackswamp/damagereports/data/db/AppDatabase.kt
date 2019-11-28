package gr.blackswamp.damagereports.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gr.blackswamp.damagereports.data.db.converter.DateConverter
import gr.blackswamp.damagereports.data.db.converter.UUIDConverter
import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.dao.DamageDao
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.db.entities.*
import java.util.*


@Database(
    entities = [BrandEntity::class, DamageEntity::class, DamagePartEntity::class, ModelEntity::class, PartEntity::class, ReportEntity::class],
    version = 1
)
@TypeConverters(DateConverter::class, UUIDConverter::class)
abstract class AppDatabase : RoomDatabase(), IDatabase {
    companion object {
        const val DATABASE = "data.db"
    }

    abstract val reportDao: ReportDao
    abstract val modelDao: ModelDao
    abstract val brandDao: BrandDao
    abstract val damageDao: DamageDao

    override suspend fun loadReportHeaders(filter: String) = reportDao.loadReportHeaders(filter)
    override suspend fun saveReport(report: ReportEntity) = reportDao.saveReport(report)
    override suspend fun deleteReportById(id: UUID) = reportDao.deleteReportById(id)

}