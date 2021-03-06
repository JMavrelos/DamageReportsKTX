package gr.blackswamp.damagereports

import android.content.Context
import androidx.room.Room
import gr.blackswamp.core.testing.randomString
import gr.blackswamp.core.util.RandomUUID
import gr.blackswamp.core.util.withAdded
import gr.blackswamp.damagereports.data.db.AppDatabase
import gr.blackswamp.damagereports.data.db.AppDatabaseImpl
import gr.blackswamp.damagereports.data.db.entities.*
import kotlinx.coroutines.runBlocking
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.util.*
import kotlin.random.Random

object TestData {

    val BRANDS by lazy {
        (0 until 100)
            .map { BrandEntity(UUID.randomUUID(), "${randomString(10)} Brand ", false) }
    }

    val MODELS: List<ModelEntity> by lazy {
        BRANDS.flatMap { brand ->
            (0 until Random.nextInt(2, 10)).map {
                ModelEntity(UUID.randomUUID(), "${randomString(10)} Model", brand.id, false)
            }
        }
    }

    val REPORTS by lazy {
        (0 until 30)
            .map {
                ReportEntity(
                    UUID.randomUUID()
                    , "${randomString(10)} Report"
                    , "${randomString(10)} description"
                    , BRANDS[40 - it].id
                    , MODELS.filter { model -> model.brand == BRANDS[40 - it].id }.random().id
                    , randomDate(10)
                    , randomDate(5)
                )
            }
    }

    val REPORT_HEADERS by lazy {
        REPORTS.map {
            ReportHeaderEntity(it.id, it.name, it.description, it.updated)
        }

    }

    val PARTS by lazy {
        (0 until 50).map {
            PartEntity(RandomUUID, "part $it", null, null, Random.nextDouble(10.0, 100.0).toBigDecimal(), false)
        }.union(
            (51 until 100).map {
                PartEntity(RandomUUID, "part $it", BRANDS.random().id, null, Random.nextDouble(10.0, 100.0).toBigDecimal(), false)
            }
        ).union(
            (101 until 200).map {
                val brand = BRANDS.random().id
                PartEntity(RandomUUID, "part $it", brand, MODELS.filter { it.brand == brand }.random().id, Random.nextDouble(10.0, 100.0).toBigDecimal(), false)
            }
        ).union(
            REPORTS.mapIndexed { idx, r ->
                PartEntity(RandomUUID, "report part $idx", r.brand, r.model, Random.nextDouble(10.0, 100.0).toBigDecimal(), false)
            }
        )
    }

    val DAMAGES: List<DamageEntity> by lazy {
        REPORTS.flatMap { r ->
            (0 until Random.nextInt(1, 3)).map {
                DamageEntity(UUID.randomUUID(), "${randomString(10)} Damage", "${randomString(10)} Damage Description", r.id)
            }
        }
    }

    val DAMAGE_PARTS: List<DamagePartEntity> by lazy {
        DAMAGES.flatMap {
            val report = REPORTS.first { r -> r.id == it.report }
            val part = PARTS.filter { (it.brand == report.brand && it.model == report.model) || (it.brand == null && it.model == null) || (it.brand == report.brand && it.model == null) }.random().id
            (0 until Random.nextInt(2, 5)).map { _ ->
                DamagePartEntity(
                    RandomUUID
                    , part
                    , it.id
                    , Date(System.currentTimeMillis() + Random.nextLong(-10000, 10000))
                    , Random.nextInt(1, 10)
                )
            }
        }
    }

    private fun randomDate(maxDaysBack: Int): Date =
        Calendar.getInstance()
            .withAdded(Calendar.DAY_OF_MONTH, Random.nextInt(-maxDaysBack, 0))
            .withAdded(Calendar.MILLISECOND, Random.nextInt(-1_000_000, 0)).time

    fun initialize(context: Context) {
        val testDb = Room.inMemoryDatabaseBuilder(context, AppDatabaseImpl::class.java).build()
        loadKoinModules(module {
            single<AppDatabase>(override = true) { testDb }
        })
        insertData(testDb)

    }

    fun build(context: Context): AppDatabaseImpl {
        val testDb = Room.databaseBuilder(context, AppDatabaseImpl::class.java, "test.db").allowMainThreadQueries().build()
        insertData(testDb)
        return testDb
    }

    private fun insertData(db: AppDatabaseImpl) {
        runBlocking {
            BRANDS.forEach {
                db.brandDao.insertBrand(it)
            }
            MODELS.forEach {
                db.modelDao.insertModel(it)

            }
            REPORTS.forEach {
                db.reportDao.insertReport(it)
            }
            PARTS.forEach {
                db.partDao.insertPart(it)
            }
            DAMAGES.forEach {
                db.damageDao.insertDamage(it)
            }
            DAMAGE_PARTS.forEach {
                db.damagePartDao.insertDamagePart(it)
            }
        }
    }

}


