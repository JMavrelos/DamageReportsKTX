package gr.blackswamp.damagereports.data.db

import gr.blackswamp.damagereports.data.db.dao.*

interface AppDatabase {
    val globalDao: GlobalDao
    val reportDao: ReportDao
    val damageDao: DamageDao
    val brandDao: BrandDao
    val modelDao: ModelDao
}