package gr.blackswamp.core.db

import gr.blackswamp.core.db.converters.LongDateConverter
import gr.blackswamp.damagereports.data.db.converters.StringDateConverter
import gr.blackswamp.damagereports.data.db.converters.UUIDConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.*

class ConverterTests {
    @Test
    fun `converts a date to long and back correctly`() {
        val date = Calendar.getInstance().time

        val converted = LongDateConverter.toDate(LongDateConverter.toLong(date))
        assertEquals(date, converted)
    }

    @Test
    fun `converts a date to string and back correctly`() {
        val date = Calendar.getInstance().time
        val converted = StringDateConverter.toDate(StringDateConverter.toText(date))
        assertEquals(date, converted)
    }

    @Test
    fun `check that the string is correctly saved as string`() {
        val expected = "20180422234923123"
        val date = Calendar.getInstance().let {
            it.set(2018, 3, 22, 23, 49, 23)
            it.set(Calendar.MILLISECOND, 123)
            it.time
        }
        val converted = StringDateConverter.toText(date)
        assertEquals(expected, converted)
    }

    @Test
    fun `converts a uuid to string and back correctly`() {
        val id = UUID.randomUUID()

        val converted = UUIDConverter.toUUID(UUIDConverter.toText(id))
        assertEquals(id, converted)
    }

    @Test
    fun `converts a null uuid to string and back`() {
        assertNull(UUIDConverter.toText(null))
        assertNull(UUIDConverter.toUUID(null))
    }

}
