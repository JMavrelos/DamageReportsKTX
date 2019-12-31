package gr.blackswamp.damagereports.util

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource

class StaticDataSource<T>(private val values: List<T>) : PageKeyedDataSource<Int, T>() {
    companion object {
        fun <T> factory(values: List<T>): Factory<Int, T> {
            return object : Factory<Int, T>() {
                override fun create(): DataSource<Int, T> {
                    return StaticDataSource(values)
                }
            }
        }
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
        callback.onResult(getValues(0, params.requestedLoadSize), null, 1)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        val nextKey = if (((params.key * params.requestedLoadSize) + params.requestedLoadSize) < values.size) params.key + 1 else null
        callback.onResult(getValues(params.key, params.requestedLoadSize), nextKey)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        val nextKey = if (params.key == 0) null else params.key - 1
        callback.onResult(getValues(params.key, params.requestedLoadSize), nextKey)
    }

    private fun getValues(page: Int, size: Int): List<T> {
        val startIdx = page * size
        if (startIdx >= values.size) return listOf()
        val endIdx = Math.min(startIdx + size, values.size)
        return values.subList(startIdx, endIdx)
    }

    class TestDataSourceFactory<T>(private val values: List<T>) : Factory<Int, T>() {
        override fun create(): DataSource<Int, T> {
            return StaticDataSource<T>(values)
        }
    }
}