package gr.blackswamp.damagereports.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import gr.blackswamp.core.db.count
import gr.blackswamp.core.db.countWhere
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.TestData
import gr.blackswamp.damagereports.data.db.dao.GlobalDao
import gr.blackswamp.damagereports.data.db.entities.DamageEntity
import gr.blackswamp.damagereports.data.db.entities.DamagePartEntity
import gr.blackswamp.damagereports.data.db.entities.PartEntity
import gr.blackswamp.damagereports.data.db.entities.ReportEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestApp::class)
class GlobalDaoTest {
    private lateinit var db: AppDatabaseImpl
    private lateinit var dao: GlobalDao

    @Suppress("SpellCheckingInspection")
    private val formatter = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext()
            , AppDatabaseImpl::class.java
        ).allowMainThreadQueries()
            .build()

        dao = db.globalDao
        initDb()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `reports are cleared correctly`() {
        runBlocking {
            val now = TestData.REPORTS.sortedBy { it.created.time }[15].updated
            val nowText = formatter.format(Date())
            val eligible = TestData.REPORTS.filter { it.updated.time < now.time }
            val reports = eligible.shuffled().take(5).map { it.id }
            reports.forEach {
                db.reportDao.flagReportDeleted(it)
            }

            val affected = dao.clearUnusedReports(nowText)

            //check reports are deleted
            assertEquals(reports.size, affected)
            assertEquals(0, db.countWhere("reports", " id in (${reports.joinToString("','", "'", "'")})"))
            assertEquals(TestData.REPORTS.size - reports.size, db.count("reports"))

            // check damages are deleted
            val damages = TestData.DAMAGES.filter { it.report in reports }.map(DamageEntity::id)
            assertEquals(0, db.countWhere("damages", " id in (${damages.joinToString("','", "'", "'")})"))
            assertEquals(TestData.DAMAGES.size - damages.size, db.count("damages"))

            val damageParts = TestData.DAMAGE_PARTS.filter { it.damage in damages }.map(DamagePartEntity::id)
            assertEquals(0, db.countWhere("damage_parts", " id in (${damageParts.joinToString("','", "'", "'")})"))
            assertEquals(TestData.DAMAGE_PARTS.size - damageParts.size, db.count("damage_parts"))

        }
    }

    @Test
    fun `parts are deleted correctly`() {
        runBlocking {
            val eligible = TestData.PARTS.filter { it.id !in TestData.DAMAGE_PARTS.map(DamagePartEntity::part) }
            val parts = eligible.shuffled().take(10).map { it.id }
            parts.forEach {
                db.partDao.flagPartDeleted(it)
            }


            val affected = dao.clearUnusedParts()

            //check reports are deleted
            assertEquals(parts.size, affected)
            assertEquals(0, db.countWhere("parts", " id in (${parts.joinToString("','", "'", "'")})"))
            assertEquals(TestData.PARTS.size - parts.size, db.count("parts"))
        }
    }

    @Test
    fun `brands are deleted correctly`() {
        runBlocking {
            val eligible = TestData.BRANDS.filter { it.id !in TestData.PARTS.map(PartEntity::brand) && it.id !in TestData.REPORTS.map(ReportEntity::brand) }
            assertNotEquals("RNG was not on your side, run the test again", 0, eligible)

            val brands = eligible.shuffled().take(5).map { it.id }
            brands.forEach {
                db.brandDao.flagBrandDeleted(it)
            }

            assertEquals(brands.size, db.countWhere("brands", " id in (${brands.joinToString("','", "'", "'")})"))

            val affected = dao.clearUnusedBrands()

            //check reports are deleted
            assertEquals(0, db.countWhere("brands", " id in (${brands.joinToString("','", "'", "'")})"))
            assertEquals(TestData.BRANDS.size - brands.size, db.count("brands"))
            assertEquals(brands.size, affected)
            assertEquals(0, db.countWhere("models", " brand in (${brands.joinToString("','", "'", "'")})")) //make sure it cascades
        }
    }

    @Test
    fun `models are deleted correctly`() {
        runBlocking {
            val eligible = TestData.MODELS.filter {
                it.id !in TestData.PARTS.map(PartEntity::model)
                        && it.id !in TestData.REPORTS.map(ReportEntity::model)
            }
            assertNotEquals("RNG was not on your side, run the test again", 0, eligible)

            val models = eligible.shuffled().take(5).map { it.id }
            models.forEach {
                db.modelDao.flagModelDeleted(it)
            }

            assertEquals(models.size, db.countWhere("models", " id in (${models.joinToString("','", "'", "'")})"))

            val affected = dao.clearUnusedModels()

            //check reports are deleted
            assertEquals(0, db.countWhere("models", " id in (${models.joinToString("','", "'", "'")})"))
            assertEquals(TestData.MODELS.size - models.size, db.count("models"))
            assertEquals(models.size, affected)
        }
    }

    private fun initDb() {
        runBlocking {
            TestData.BRANDS.forEach { db.brandDao.insertBrand(it) }
            TestData.MODELS.forEach { db.modelDao.insertModel(it) }
            TestData.REPORTS.forEach { db.reportDao.insertReport(it) }
            TestData.PARTS.forEach { db.partDao.insertPart(it) }
            TestData.DAMAGES.forEach { db.damageDao.insertDamage(it) }
            TestData.DAMAGE_PARTS.forEach { db.damagePartDao.insertDamagePart(it) }
        }
    }

}