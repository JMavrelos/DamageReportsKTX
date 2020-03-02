package gr.blackswamp.core.testing

import gr.blackswamp.core.coroutines.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
object TestDispatcher : Dispatcher {
    override val IO = Dispatchers.Unconfined
    override val CPU = Dispatchers.Unconfined
    override val UI = Dispatchers.Unconfined
    override val Immediate = Dispatchers.Unconfined
}