package gr.blackswamp.core.schedulers

import kotlinx.coroutines.Dispatchers

object TestDispatchers : IDispatchers {
    override val IO = Dispatchers.Unconfined
    override val Main = Dispatchers.Unconfined
    override val CPU = Dispatchers.Unconfined
}