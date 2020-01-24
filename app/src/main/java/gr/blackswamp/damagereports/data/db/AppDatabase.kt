package gr.blackswamp.damagereports.data.db

import gr.blackswamp.damagereports.data.db.dao.BrandDao
import gr.blackswamp.damagereports.data.db.dao.DamageDao
import gr.blackswamp.damagereports.data.db.dao.ModelDao
import gr.blackswamp.damagereports.data.db.dao.ReportDao
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import gr.blackswamp.damagereports.data.db.entities.ReportHeaderEntity
import java.util.*

interface AppDatabase {
    val reportDao:ReportDao
    val damageDao: DamageDao
    val brandDao: BrandDao
    val modelDao: ModelDao
}