package gr.blackswamp.core.util

import androidx.annotation.VisibleForTesting
import java.text.SimpleDateFormat
import java.util.*

@Suppress("SpellCheckingInspection")
@VisibleForTesting
internal val formatters = mutableMapOf<String, SimpleDateFormat>()

fun Date.asString(pattern: String): String {
    val formatter = formatters[pattern] ?: SimpleDateFormat(pattern, Locale.getDefault()).apply {
        formatters.put(pattern, this)
    }
    return formatter.format(this)
}

private fun Calendar.getFormatted(field: Int, chars: Int = 0): String {
    val reply = this.get(field).toString()
    return "0".repeat((chars - reply.length).coerceAtLeast(0)) + reply
}

fun Date.toDateString() = this.asString("yyyy-MM-dd")

fun Date.toDateTimeString() = this.asString("yyyy-MM-dd HH:mm:ss")

fun Date.toTimestamp() = this.asString("yyyyMMddHHmmss")

val EmptyUUID by lazy { UUID(0L, 0L) }