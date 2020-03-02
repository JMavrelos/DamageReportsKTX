package gr.blackswamp.core.coroutines

import kotlinx.coroutines.Dispatchers

object DispatcherImpl : Dispatcher {
    override val UI = Dispatchers.Main
    override val CPU = Dispatchers.Default
    override val IO = Dispatchers.IO
    override val Immediate = Dispatchers.Main.immediate
}