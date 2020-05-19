package gr.blackswamp.damagereports.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import gr.blackswamp.damagereports.data.db.converters.StringDateConverter
import java.util.*

@Dao
abstract class GlobalDao {
    @Transaction
    open suspend fun clearUnused() {
        val lastWeek = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -7)
        }.time.let {
            StringDateConverter.toText(it)
        }

        clearUnusedReports(lastWeek)
        clearUnusedDamages()
        clearUnusedDamageParts()
        clearUnusedParts()
        clearUnusedBrands()
        clearUnusedModels()
    }

    @Query("DELETE FROM reports WHERE deleted = 1 AND updated < :before")
    abstract suspend fun clearUnusedReports(before: String)

    @Query("DELETE FROM damages WHERE report NOT IN (SELECT id FROM reports)")
    abstract suspend fun clearUnusedDamages()


    @Query("DELETE FROM brands WHERE deleted AND id not in (SELECT brand FROM reports) AND id NOT IN (SELECT brand FROM parts)")
    abstract suspend fun clearUnusedBrands()

    @Query("DELETE FROM models WHERE deleted AND id NOT IN (SELECT model FROM reports) AND id NOT IN (SELECT model FROM parts)")
    abstract suspend fun clearUnusedModels()

    @Query("DELETE FROM damage_parts WHERE damage NOT IN (SELECT id FROM damages)")
    abstract suspend fun clearUnusedDamageParts()

    @Query("DELETE FROM parts WHERE deleted AND id NOT IN (SELECT part FROM damage_parts) ")
    abstract suspend fun clearUnusedParts()

}