package gr.blackswamp.core.schedulers

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchers {
    val IO : CoroutineDispatcher
    val Main : CoroutineDispatcher
    val CPU : CoroutineDispatcher
}