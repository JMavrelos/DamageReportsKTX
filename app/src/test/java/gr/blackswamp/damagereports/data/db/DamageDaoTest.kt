package gr.blackswamp.damagereports.data.db

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import gr.blackswamp.core.db.countWhere
import gr.blackswamp.core.testing.getOrAwait
import gr.blackswamp.core.util.withAdded
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.TestData
import gr.blackswamp.damagereports.data.db.converters.StringDateConverter
import gr.blackswamp.damagereports.data.db.dao.DamageDao
import gr.blackswamp.damagereports.data.db.entities.DamageEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*
import java.util.UUID.randomUUID

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestApp::class)
class DamageDaoTest {
    private lateinit var db: AppDatabaseImpl
    private lateinit var dao: DamageDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var thrown: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {
        db = TestData.build(ApplicationProvider.getApplicationContext())
        dao = db.damageDao
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `insert damage that does not correspond to a report throws an error`() {
        runBlockingTest {
            val damage = DamageEntity(randomUUID(), "test", "test", randomUUID())
            thrown.expect(SQLiteException::class.java)
            dao.insertDamage(damage)
        }
    }

    @Test
    fun `insert damage normally`() {
        runBlockingTest {
            val damage = DamageEntity(randomUUID(), "test", "test", TestData.REPORTS.random().id)
            dao.insertDamage(damage)
        }
    }

    @Test
    fun `delete works correctly`() {
        runBlockingTest {
            val damage = TestData.DAMAGES.random()
            dao.deleteDamageById(damage.id)

            assertEquals(0, db.countWhere("damages", "id = '${damage.id}'"))
        }
    }

    @Test
    fun `deleting a report deletes all corresponding damages`() {
        runBlockingTest {
            val report = TestData.REPORTS.random().copy()
            val damages = TestData.DAMAGES.filter { it.report == report.id }

            db.reportDao.flagReportDeleted(report.id)
            val before = StringDateConverter.toText(Calendar.getInstance().withAdded(Calendar.DAY_OF_MONTH, 100).time)
            db.globalDao.clearUnusedReports(before)

            damages.forEach { damage ->
                assertEquals(0, db.countWhere("damages", "id = '${damage.id}'"))
            }

        }
    }

    @Test
    fun `loading damages works`() {
        runBlockingTest {
            val report = TestData.REPORTS.random()
            val damages = TestData.DAMAGES.filter { it.report == report.id }

            val data = dao.loadDamageHeadersForReport(report.id).toLiveData(1000).getOrAwait().toList()

            assertEquals(damages.size, data.size)

            damages.forEach { damage ->
                val entity = data.firstOrNull { it.id == damage.id }!!
                assertEquals(damage.description, entity.name)
                //todo: fix after I figure out how to handle pictures
                assertEquals(3, entity.pictures)

                //todo:continue from here
                // val parts = db.countWhere("damage_parts", " damage = '${damage.id}' ")
                //assertEquals(damage.description, entity.parts)
            }

        }
    }
}