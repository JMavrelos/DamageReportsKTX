package gr.blackswamp.damagereports

import gr.blackswamp.core.testing.randomDate
import gr.blackswamp.core.testing.randomString
import gr.blackswamp.core.util.RandomUUID
import gr.blackswamp.damagereports.data.db.entities.*
import java.util.*
import kotlin.random.Random

object UnitTestData {

    val BRANDS = (0 until 100)
        .map { BrandEntity(UUID.randomUUID(), "${randomString(10)} Brand ", false) }

    val MODELS: List<ModelEntity> = BRANDS.flatMap { brand ->
        (1 until Random.nextInt(2, 10)).map {
            ModelEntity(UUID.randomUUID(), "${randomString(10)} Model", brand.id, false)
        }
    }

    val REPORTS = (0 until 30)
        .map {
            ReportEntity(
                UUID.randomUUID()
                , "${randomString(10)} Report"
                , "${randomString(10)} description"
                , BRANDS[40 - it].id
                , MODELS.filter { model -> model.brand == BRANDS[40 - it].id }.random().id
                , Date(System.currentTimeMillis() + Random.nextLong(-100_000, +100_000))
            )
        }

    val REPORT_HEADERS = (0 until 100)
        .map {
            ReportHeaderEntity(
                UUID.randomUUID(), "${randomString(10)} header $it", "${randomString(10)} description $it ",
                randomDate()
            )
        }

    val PARTS = (0 until 50).map {
        PartEntity(RandomUUID, "part $it", null, null, Random.nextDouble(10.0, 100.0), false)
    }.union(
        (51 until 100).map {
            PartEntity(RandomUUID, "part $it", BRANDS.random().id, null, Random.nextDouble(10.0, 100.0), false)
        }
    ).union(
        (101 until 200).map {
            val brand = BRANDS.random().id
            PartEntity(RandomUUID, "part $it", brand, MODELS.filter { it.brand == brand }.random().id, Random.nextDouble(10.0, 100.0), false)
        }
    ).union(
        REPORTS.mapIndexed { idx, r ->
            PartEntity(RandomUUID, "report part $idx", r.brand, r.model, Random.nextDouble(10.0, 100.0), false)
        }
    )

    val DAMAGES: List<DamageEntity> =
        REPORTS.shuffled().take(20).flatMap { r ->
            (1 until Random.nextInt(1, 3)).map {
                DamageEntity(UUID.randomUUID(), "${randomString(10)} Damage", "${randomString(10)} Damage Description", r.id)
            }
        }

    val DAMAGE_PARTS: List<DamagePartEntity> =
        DAMAGES.flatMap {
            val report = REPORTS.first { r -> r.id == it.report }
            val part = PARTS.filter { it.brand == report.brand && it.model == report.model }.random().id
            (1 until Random.nextInt(2, 5)).map { _ ->
                DamagePartEntity(
                    RandomUUID
                    , part
                    , it.id
                    , Date(System.currentTimeMillis() + Random.nextLong(-10000, 10000))
                    , Random.nextInt(1, 10)
                )
            }
        }

}