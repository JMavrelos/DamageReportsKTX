package gr.blackswamp.damagereports.ui.model

import java.util.*

interface ReportHeader {
    val id: UUID
    val name: String
    val description: String
    val date: Date
}