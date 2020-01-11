package gr.blackswamp.damagereports.data.db.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {
    companion object {
        @JvmStatic
        val formatter = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH)

        @TypeConverter
        @JvmStatic
        fun toDate(text: String): Date = formatter.parse(text)

        @TypeConverter
        @JvmStatic
        fun toText(date: Date): String = formatter.format(date)
    }
}