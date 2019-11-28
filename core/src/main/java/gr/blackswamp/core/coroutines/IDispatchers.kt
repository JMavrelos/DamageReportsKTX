package gr.blackswamp.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchers {
    val IO: CoroutineDispatcher
    val CPU: CoroutineDispatcher
    val UI: CoroutineDispatcher
}