package gr.blackswamp.core.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PageKeyedDataSource
import gr.blackswamp.core.db.paging.StaticDataSource
import gr.blackswamp.core.testing.randomStringList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PagingTests {
    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()


    private val initial = object : PageKeyedDataSource.LoadInitialCallback<Int, String>() {

        var previous: Int? = null
        var total: Int = 0
        var retrieved = listOf<String>()
        var next: Int? = null

        override fun onResult(data: MutableList<String>, position: Int, totalCount: Int, previousPageKey: Int?, nextPageKey: Int?) {
            retrieved = data
            total = totalCount
            previous = previousPageKey
            next = nextPageKey
        }

        override fun onResult(data: MutableList<String>, previousPageKey: Int?, nextPageKey: Int?) {
            retrieved = data
            total = -1
            previous = previousPageKey
            next = nextPageKey
        }

        fun reset() {
            previous = null
            total = 0
            retrieved = listOf()
            next = null
        }
    }

    private val callback = object : PageKeyedDataSource.LoadCallback<Int, String>() {
        var retrieved = listOf<String>()
        var adjacent: Int? = null

        override fun onResult(data: MutableList<String>, adjacentPageKey: Int?) {
            retrieved = data
            adjacent = adjacentPageKey
        }

        fun reset() {
            retrieved = listOf()
            adjacent = null
        }
    }

    @Before
    fun setup() {
        initial.reset()
        callback.reset()
    }

    @Test
    fun `check that a load initial works`() {
        val list = randomStringList(43, 10)
        val ds = StaticDataSource.factory(list).create() as PageKeyedDataSource
        val params = PageKeyedDataSource.LoadInitialParams<Int>(10, true)

        ds.loadInitial(params, initial)

        assertEquals(list.subList(0, 10), initial.retrieved)
        assertEquals(1, initial.next)
        assertEquals(null, initial.previous)
        assertEquals(43, initial.total)
    }

    @Test
    fun `check that load before works`() {
        val list = randomStringList(50, 10)
        val ds = StaticDataSource.factory(list).create() as PageKeyedDataSource

        val params = PageKeyedDataSource.LoadParams(2, 7)

        ds.loadBefore(params, callback)

        assertEquals(list.subList(7, 14), callback.retrieved)
        assertEquals(1, callback.adjacent)
    }

    @Test
    fun `check that load before works up to the beggining of the list `() {
        val list = randomStringList(50, 10)
        val ds = StaticDataSource.factory(list).create() as PageKeyedDataSource

        val params = PageKeyedDataSource.LoadParams(1, 7)

        ds.loadBefore(params, callback)

        assertEquals(list.subList(0, 7), callback.retrieved)
        assertEquals(0, callback.adjacent)
    }

    @Test
    fun `check that load before returns an empty key at the start`() {
        val list = randomStringList(50, 10)
        val ds = StaticDataSource.factory(list).create() as PageKeyedDataSource

        val params = PageKeyedDataSource.LoadParams(0, 7)

        ds.loadBefore(params, callback)

        assertEquals(listOf<String>(), callback.retrieved)
        assertEquals(null, callback.adjacent)
    }

    @Test
    fun `check that load after works`() {
        val list = randomStringList(50, 10)
        val ds = StaticDataSource.factory(list).create() as PageKeyedDataSource

        val params = PageKeyedDataSource.LoadParams(2, 7)

        ds.loadAfter(params, callback)

        assertEquals(list.subList(14, 21), callback.retrieved)
        assertEquals(3, callback.adjacent)
    }

    @Test
    fun `check that load past end list works with partial data`() {
        val list = randomStringList(10, 10)
        val ds = StaticDataSource.factory(list).create() as PageKeyedDataSource

        val params = PageKeyedDataSource.LoadParams(1, 7)

        ds.loadAfter(params, callback)

        assertEquals(list.subList(7, 10), callback.retrieved)
        assertEquals(null, callback.adjacent)
    }

    @Test
    fun `check that load past end list works with empty data`() {
        val list = randomStringList(10, 10)
        val ds = StaticDataSource.factory(list).create() as PageKeyedDataSource

        val params = PageKeyedDataSource.LoadParams(3, 7)

        ds.loadAfter(params, callback)

        assertEquals(listOf<String>(), callback.retrieved)
        assertEquals(null, callback.adjacent)
    }

    @Test
    fun `check that with totals works for the source`() {
        val list = randomStringList(100, 10)
        val ds = StaticDataSource.factory(list, true).create() as PageKeyedDataSource
        val params = PageKeyedDataSource.LoadInitialParams<Int>(10, true)

        ds.loadInitial(params, initial)

        assertEquals(list.subList(0, 10), initial.retrieved)
        assertEquals(1, initial.next)
        assertEquals(null, initial.previous)
        assertEquals(100, initial.total)
    }

    @Test
    fun `check that without totals works for the source`() {
        val list = randomStringList(100, 10)
        val ds = StaticDataSource.factory(list, false).create() as PageKeyedDataSource
        val params = PageKeyedDataSource.LoadInitialParams<Int>(10, true)

        ds.loadInitial(params, initial)

        assertEquals(list.subList(0, 10), initial.retrieved)
        assertEquals(1, initial.next)
        assertEquals(null, initial.previous)
        assertEquals(-1, initial.total)
    }


}