package gr.blackswamp.core.schedulers

import kotlinx.coroutines.Dispatchers

object AppDispatchers : IDispatchers {
    override val IO = Dispatchers.IO
    override val Main = Dispatchers.Main
    override val CPU = Dispatchers.Default
}