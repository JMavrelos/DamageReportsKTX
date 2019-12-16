package gr.blackswamp.core

import gr.blackswamp.core.coroutines.IDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
object TestDispatchers : IDispatchers {
    override val IO = Dispatchers.Unconfined
    override val CPU = Dispatchers.Unconfined
    override val UI = Dispatchers.Unconfined
}