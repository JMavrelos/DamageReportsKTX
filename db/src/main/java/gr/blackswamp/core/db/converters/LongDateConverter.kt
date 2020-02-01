package gr.blackswamp.core.db.converters

import androidx.room.TypeConverter
import java.util.*

class LongDateConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toDate(timestamp: Long): Date = Date(timestamp)

        @TypeConverter
        @JvmStatic
        fun toLong(date: Date): Long = date.time
    }
}