package gr.blackswamp.core.data

class Response<T : Any?>(private val value: Any?) {
    companion object {
        fun <T> success(value: T): Response<T> = Response(value)
        fun <T> failure(exception: Throwable): Response<T> = Response(Failure(exception))
        fun <T> failure(message: String, cause: Throwable? = null): Response<T> = Response(Failure(Throwable(message, cause)))
    }

    val hasError: Boolean get() = value is Failure

    @Suppress("UNCHECKED_CAST")
    val get: T
        get() = value as T

    val error: Throwable
        get() = (value as Failure).exception

    internal data class Failure(val exception: Throwable)

}