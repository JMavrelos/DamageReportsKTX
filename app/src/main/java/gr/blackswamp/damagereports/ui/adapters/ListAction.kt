package gr.blackswamp.damagereports.ui.adapters

import java.util.*

interface ListAction {
    fun delete(id: UUID)
    fun select(id: UUID)
    fun edit(id: UUID)
}
