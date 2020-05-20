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

        clearUnusedReports(lastWeek) //this will cascade to damages and damage parts
        clearUnusedParts()
        clearUnusedBrands()
        clearUnusedModels()
    }

    @Query("DELETE FROM reports WHERE deleted = 1 AND updated < :before")
    abstract suspend fun clearUnusedReports(before: String): Int

    @Query("DELETE FROM brands WHERE deleted AND id NOT IN (SELECT brand FROM reports) AND id NOT IN (SELECT brand FROM parts WHERE brand IS NOT NULL )")
    abstract suspend fun clearUnusedBrands(): Int

    @Query("DELETE FROM models WHERE deleted AND id NOT IN (SELECT model FROM reports) AND id NOT IN (SELECT model FROM parts WHERE model IS NOT NULL)")
    abstract suspend fun clearUnusedModels(): Int

    @Query("DELETE FROM parts WHERE deleted AND id NOT IN (SELECT part FROM damage_parts) ")
    abstract suspend fun clearUnusedParts(): Int

}