package gr.blackswamp.core.testing

import gr.blackswamp.core.coroutines.IDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TestDispatchers : IDispatchers {
    override val IO = Dispatchers.Unconfined
    override val CPU = Dispatchers.Unconfined
    override val UI = Dispatchers.Unconfined
}