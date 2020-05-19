package gr.blackswamp.core.util

import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


val EmptyUUID by lazy { UUID(0L, 0L) }

val RandomUUID: UUID get() = UUID.randomUUID()

@ExperimentalContracts
fun UUID?.isNullOrBlank(): Boolean {
    contract {
        returns(false) implies (this@isNullOrBlank != null)
    }
    return this == null || this == EmptyUUID
}