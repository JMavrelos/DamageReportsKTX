package gr.blackswamp.damagereports.ui.model

import java.util.*

interface Report {
    val id: UUID
    val name: String
    val description: String
    val brandName: String?
    val modelName: String?
    val created: Date
    val changed: Boolean
}