package gr.blackswamp.damagereports.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import gr.blackswamp.damagereports.TestApp
import gr.blackswamp.damagereports.data.db.dao.GlobalDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(application = TestApp::class)
class GlobalDaoTest {
    private lateinit var db: AppDatabaseImpl
    private lateinit var dao: GlobalDao

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
    }

    @After
    fun tearDown() {
        db.close()
    }


    @Test
    @Ignore("todo:add the actual test")
    fun `clear unused`() {
        runBlockingTest {
            dao.clearUnused()
        }
    }


}