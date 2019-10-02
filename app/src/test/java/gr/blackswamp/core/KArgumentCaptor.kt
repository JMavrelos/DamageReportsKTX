package gr.blackswamp.core

import org.mockito.ArgumentCaptor
import kotlin.reflect.KClass

class KArgumentCaptor<out T : Any?>(
    private val captor: ArgumentCaptor<T>,
    private val tClass: KClass<*>
) {

    val firstValue: T
        get() = captor.allValues[0]

    val secondValue: T
        get() = captor.allValues[1]

    val thirdValue: T
        get() = captor.allValues[2]

    val lastValue: T
        get() = captor.allValues.last()

    val allValues: List<T>
        get() = captor.allValues

    @Suppress("UNCHECKED_CAST")
    fun capture(): T {
        return captor.capture() ?: createInstance(tClass) as T
    }
}

inline fun <reified T : Any> createInstance(): T {
    return createInstance(T::class)
}

fun <T : Any> createInstance(kClass: KClass<T>): T {
    return castNull()
}

@Suppress("UNCHECKED_CAST")
private fun <T> castNull(): T = null as T

inline fun <reified T : Any> argumentCaptor(): KArgumentCaptor<T> {
    return KArgumentCaptor(ArgumentCaptor.forClass(T::class.java), T::class)
}

inline fun <reified T : Any> argumentCaptor(f: KArgumentCaptor<T>.() -> Unit): KArgumentCaptor<T> {
    return argumentCaptor<T>().apply(f)
}

inline fun <reified T : Any> nullableArgumentCaptor(): KArgumentCaptor<T?> {
    return KArgumentCaptor(ArgumentCaptor.forClass(T::class.java), T::class)
}

inline fun <reified T : Any> nullableArgumentCaptor(f: KArgumentCaptor<T?>.() -> Unit): KArgumentCaptor<T?> {
    return nullableArgumentCaptor<T>().apply(f)
}

inline fun <reified T : Any> capture(captor: ArgumentCaptor<T>): T {
    return captor.capture() ?: createInstance()
}

