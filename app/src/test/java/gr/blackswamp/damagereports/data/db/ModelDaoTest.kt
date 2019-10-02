package gr.blackswamp.damagereports.data.db
//
//import android.database.sqlite.SQLiteConstraintException
//import android.database.sqlite.SQLiteException
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.test.InstrumentationRegistry
//import gr.blackswamp.damagereports.data.TestData
//import gr.blackswamp.damagereports.data.count
//import gr.blackswamp.damagereports.data.countWhere
//import gr.blackswamp.damagereports.data.db.dao.ModelDao
//import gr.blackswamp.damagereports.data.db.entities.ModelEntity
//import gr.blackswamp.damagereports.data.db.entities.ReportEntity
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import java.util.*
//
//class ModelDaoTest {
//    private lateinit var db: AppDatabase
//    private lateinit var dao: ModelDao
//
//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//
//    @Before
//    fun setUp() {
//        db = Room.inMemoryDatabaseBuilder(
//            InstrumentationRegistry.getContext(),
//            AppDatabase::class.java
//        )
//            .allowMainThreadQueries()
//            .build()
//
//        dao = db.modelDao
//    }
//
//    @After
//    fun tearDown() {
//        (db as RoomDatabase).close()
//    }
//
//    @Test
//    fun insertNewModel() {
//        val brand = TestData.BRANDS[0]
//        val model = TestData.MODELS.first { it.brand == brand.id }
//        db.brandDao.saveBrand(brand).test()
//        val observer = dao.saveModel(model).test()
//        observer.assertComplete()
//        assertEquals(1, db.count("models"))
//    }
//
//    @Test
//    fun insertWithInvalidBrandFails() {
//        val brand = TestData.BRANDS[0]
//        val model = TestData.MODELS.first { it.brand == brand.id }
//        val observer = dao.saveModel(model).test()
//        observer.assertError(SQLiteConstraintException::class.java)
//            .assertError { it.message!!.contains("FOREIGN KEY") }
//
//        assertEquals(0, db.count("models"))
//    }
//
//    @Test
//    fun updateModel() {
//        initModels()
//        val updated = TestData.MODELS[2].copy(name = "this is the new thang")
//        dao.saveModel(updated).test().assertComplete().assertNoErrors()
//
//        assertEquals(TestData.MODELS.size + TestData.DELETED_MODELS.size, db.count("models"))
//        assertEquals(1, db.countWhere("models", " name = '${updated.name}'"))
//    }
//
//    @Test
//    fun searchModelsWithNoArgs() {
//        initModels()
//        val observer = dao.searchModels("").test()
//        observer.assertNotComplete() // because it is an observable it does not complete
//        observer.assertNoErrors()
//        observer.assertValueCount(1)
//
//        val loaded = observer.values()[0]
//
//        assertEquals(TestData.MODELS.size, loaded.count { l -> TestData.MODELS.any { it == l } })
//    }
//
//    @Test
//    fun searchModelsWithPaging() {
//        initModels()
//        val observer = dao.searchModels("", 10, 20).test()
//        observer.assertNotComplete() // because it is an observable it does not complete
//        observer.assertNoErrors()
//        observer.assertValueCount(1)
//        val loaded = observer.values()[0]
//        assertEquals(TestData.MODELS.sortedBy { it.name }.subList(10, 30), loaded)
//    }
//
//    @Test
//    fun searchModelsWithSearch() {
//        initModels()
//        val filter =
//            "Hello World" //this is on purpose 11 characters so that the random models cannot possibly contain it in their name
//        val expected = listOf(
//            ModelEntity(UUID.randomUUID(), "5${filter}1", TestData.BRANDS[70].id, false)
//            , ModelEntity(UUID.randomUUID(), "2${filter}2", TestData.BRANDS[70].id, false)
//            , ModelEntity(UUID.randomUUID(), "3${filter}3", TestData.BRANDS[70].id, false)
//            , ModelEntity(UUID.randomUUID(), "1${filter}4", TestData.BRANDS[70].id, false)
//        )
//        expected.forEach { dao.saveModel(it).test() }
//
//        val observer = dao.searchModels(filter).test()
//        observer.assertNotComplete() // because it is an observable it does not complete
//        observer.assertNoErrors()
//        observer.assertValueCount(1)
//        val loaded = observer.values()[0]
//        assertEquals(expected.sortedBy { it.name }, loaded)
//    }
//
//
//    @Test
//    fun searchModelsWithSearchAndPaging() {
//        initModels()
//        val filter =
//            "Hello World" //this is on purpose 11 characters so that the random models cannot possibly contain it in their name
//        val searched = listOf(
//            ModelEntity(UUID.randomUUID(), "5${filter}1", TestData.BRANDS[70].id, false)
//            , ModelEntity(UUID.randomUUID(), "2${filter}2", TestData.BRANDS[70].id, false)
//            , ModelEntity(UUID.randomUUID(), "3${filter}3", TestData.BRANDS[70].id, false)
//            , ModelEntity(UUID.randomUUID(), "1${filter}4", TestData.BRANDS[70].id, false)
//        )
//        searched.forEach { dao.saveModel(it).test() }
//
//        val observer = dao.searchModels(filter, 1, 2).test()
//        observer.assertNotComplete() // because it is an observable it does not complete
//        observer.assertNoErrors()
//        observer.assertValueCount(1)
//        val loaded = observer.values()[0]
//        assertEquals(searched.sortedBy { it.name }.subList(1, 3), loaded)
//    }
//
//    @Test
//    fun deleteModel() {
//        initModels()
//        val deleted = TestData.MODELS[3].id
//        dao.deleteModelById(deleted)
//            .test()
//            .assertNoErrors()
//            .assertComplete()
//
//        assertEquals(0, db.countWhere("models", " id = '$deleted'"))
//    }
//
//    @Test
//    fun deleteModelUsedFails() {
//        initModels()
//        val toDelete = TestData.MODELS[3]
//        db.reportDao.saveReport(ReportEntity(UUID.randomUUID(), "123", "123", toDelete.brand, toDelete.id))
//            .test().assertComplete().assertNoErrors()
//
//        dao.deleteModelById(toDelete.id)
//            .test()
//            .assertNotComplete()
//            .assertError(SQLiteException::class.java)
//            .assertError { it.message!!.contains("FOREIGN KEY") }
//
//    }
//
//    private fun initModels() {
//        db.runInTransaction {
//            TestData.BRANDS.union(TestData.DELETED_BRANDS).forEach {
//                db.brandDao.saveBrand(it).test().assertNoErrors().assertComplete()
//            }
//            TestData.MODELS.union(TestData.DELETED_MODELS).forEach {
//                dao.saveModel(it).test().assertNoErrors().assertComplete()
//            }
//        }
//    }
//
//}