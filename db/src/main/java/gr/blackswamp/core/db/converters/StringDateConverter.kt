package gr.blackswamp.damagereports.data.db.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class StringDateConverter {
    companion object {
        @JvmStatic
        @Suppress("SpellCheckingInspection")
        val formatter by lazy { SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH) }

        @TypeConverter
        @JvmStatic
        fun toDate(text: String): Date = formatter.parse(text)

        @TypeConverter
        @JvmStatic
        fun toText(date: Date): String = formatter.format(date)
    }
}