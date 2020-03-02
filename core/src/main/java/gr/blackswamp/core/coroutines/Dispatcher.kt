package gr.blackswamp.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

@Suppress("PropertyName")
interface Dispatcher {
    val IO: CoroutineDispatcher
    val CPU: CoroutineDispatcher
    val UI: CoroutineDispatcher
    val Immediate: CoroutineDispatcher
}