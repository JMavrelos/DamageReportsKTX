package gr.blackswamp.core.util

import java.util.*

private val sYear by lazy { Regex("yyyy") }
private val sShortYear by lazy { Regex("yy") }
private val sMonth by lazy { Regex("MM") }
private val sShortMonth by lazy { Regex("M") }
private val sDay by lazy { Regex("dd") }
private val sShortDay by lazy { Regex("d") }
private val sHour24 by lazy { Regex("HH") }
private val sShortHour24 by lazy { Regex("H") }
private val sHour by lazy { Regex("hh") }
private val sShortHour by lazy { Regex("h") }
private val sMinute by lazy { Regex("mm") }
private val sShortMinute by lazy { Regex("m") }
private val sSeconds by lazy { Regex("s*") }
private val sMilliseconds by lazy { Regex("S*") }


fun Date.asString(pattern: String) {

    val calendar = GregorianCalendar().apply { time = this@asString }
    val builder = StringBuilder(pattern)
        .replace(sYear, calendar.getFormatted(Calendar.YEAR, 2))
        .replace(sShortYear, calendar.getFormatted(Calendar.YEAR))
        .replace(sMonth, calendar.getFormatted(Calendar.MONTH, 2))
        .replace(sShortMonth, calendar.getFormatted(Calendar.MONTH))
        .replace(sDay, calendar.getFormatted(Calendar.DAY_OF_MONTH, 2))
        .replace(sShortDay, calendar.getFormatted(Calendar.DAY_OF_MONTH))
        .replace(sHour24, calendar.getFormatted(Calendar.HOUR_OF_DAY, 2))
        .replace(sShortHour24, calendar.getFormatted(Calendar.HOUR_OF_DAY))
        .replace(sHour, calendar.getFormatted(Calendar.HOUR, 2))
        .replace(sShortHour, calendar.getFormatted(Calendar.HOUR))
        .replace(sMinute, calendar.getFormatted(Calendar.MINUTE, 2))
        .replace(sShortMinute, calendar.getFormatted(Calendar.MINUTE))
        .replace(sSeconds, calendar.getFormatted(Calendar.SECOND, 2))
        .replace(sMilliseconds, calendar.getFormatted(Calendar.MILLISECOND, 2))
}

private fun Calendar.getFormatted(field: Int, chars: Int = 0): String {
    val reply = this.get(field).toString(3)
    return "0".repeat((chars - reply.length).coerceAtLeast(0)) + reply
}

fun Date.toDateString() = this.asString("yyyy-MM-dd")

fun Date.toDateTimeString() = this.asString("yyyy-MM-dd HH:mm:ss")