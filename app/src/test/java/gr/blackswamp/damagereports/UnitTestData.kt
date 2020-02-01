package gr.blackswamp.damagereports

import gr.blackswamp.core.testing.randomDate
import gr.blackswamp.core.testing.randomString
import gr.blackswamp.damagereports.data.db.entities.*
import java.util.*

object UnitTestData {
    //The first 50 brands have models, the others do not
    val BRANDS = (0 until 100)
        .map { BrandEntity(UUID.randomUUID(), "${randomString(10)} Brand ", false) }

    val MODELS = (0 until 200)
        .map { ModelEntity(UUID.randomUUID(), "${randomString(10)} Model", BRANDS[it / 2].id, false) }

    val DELETED_BRANDS = listOf(
        BrandEntity(UUID.randomUUID(), "${randomString(10)} Deleted Brand 1", true)
        , BrandEntity(UUID.randomUUID(), "${randomString(10)} Deleted Brand 2", true)
    )

    val DELETED_MODELS = listOf(
        ModelEntity(UUID.randomUUID(), "${randomString(10)} DBrand 1 DModel 1", DELETED_BRANDS[0].id, true)
        , ModelEntity(UUID.randomUUID(), "${randomString(10)} DBrand 1 DModel 2", DELETED_BRANDS[0].id, true)
        , ModelEntity(UUID.randomUUID(), "${randomString(10)} Brand 1 Deleted Model 3", BRANDS[0].id, true)
    )

    val REPORTS = (0 until 30)
        .map {
            ReportEntity(
                UUID.randomUUID(), "${randomString(10)} Report", "${randomString(10)} description",
                BRANDS[40 - it].id, MODELS.first { model -> model.brand == BRANDS[40 - it].id }.id, Date(System.currentTimeMillis() + (it * 100))
            )
        }

    val REPORT_HEADERS = (0 until 100)
        .map {
            ReportHeaderEntity(
                UUID.randomUUID(), "${randomString(10)} header $it", "${randomString(10)} description $it ",
                randomDate()
            )
        }

    val DAMAGES: List<DamageEntity> =
        (0 until 60).map { DamageEntity(UUID.randomUUID(), "${randomString(10)} Damage", "${randomString(10)} Damage Description", REPORTS[it / 2].id) }

}