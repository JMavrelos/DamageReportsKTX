package gr.blackswamp.core.db.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource

class StaticDataSource<T>(private val values: List<T>) : PageKeyedDataSource<Int, T>() {
    companion object {
        fun <T> factory(values: List<T>, withTotal: Boolean = true): Factory<Int, T> {
            return object : Factory<Int, T>() {
                override fun create(): DataSource<Int, T> {
                    return StaticDataSource(values).apply {
                        loadTotals = withTotal
                    }
                }
            }
        }
    }

    private var loadTotals = true

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
        if (loadTotals)
            callback.onResult(getValues(0, params.requestedLoadSize), 0, values.size, null, 1)
        else
            callback.onResult(getValues(0, params.requestedLoadSize), null, 1)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        val nextKey = if (((params.key * params.requestedLoadSize) + params.requestedLoadSize) < values.size) params.key + 1 else null
        callback.onResult(getValues(params.key, params.requestedLoadSize), nextKey)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        if (params.key <= 0) {
            callback.onResult(listOf(), null)
        } else {
            callback.onResult(getValues(params.key - 1, params.requestedLoadSize), params.key - 1)
        }
    }

    private fun getValues(page: Int, size: Int): List<T> {
        val startIdx = page * size
        if (startIdx >= values.size) return listOf()
        val endIdx = (startIdx + size).coerceAtMost(values.size)
        return values.subList(startIdx, endIdx)
    }
}