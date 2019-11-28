package gr.blackswamp.core.coroutines

import kotlinx.coroutines.Dispatchers

object AppDispatchers  : IDispatchers{
    override val UI = Dispatchers.Main
    override val CPU = Dispatchers.Default
    override val IO = Dispatchers.IO
}