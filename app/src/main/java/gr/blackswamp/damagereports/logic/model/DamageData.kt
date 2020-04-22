package gr.blackswamp.damagereports.logic.model

import gr.blackswamp.damagereports.ui.model.ReportDamage

data class DamageData(override val name: String, override val pictures: Int, override val parts: Int, override val cost: Double) : ReportDamage