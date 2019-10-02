package gr.blackswamp.damagereports.ui.model

import java.util.*

interface Report {
    val id:UUID
    val name: String
    val description: String
    val brand: String?
    val model: String?
    val created: Date
}