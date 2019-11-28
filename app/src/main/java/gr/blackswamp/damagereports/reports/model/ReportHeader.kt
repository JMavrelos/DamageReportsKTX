package gr.blackswamp.damagereports.reports.model

import java.util.*

interface ReportHeader {
    val id: UUID
    val name: String
    val description: String
    val date: Date
}