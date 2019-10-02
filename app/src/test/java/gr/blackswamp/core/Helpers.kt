package gr.blackswamp.core

import java.util.*

object Helpers {
    private val rnd = Random(System.currentTimeMillis())

    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun randomString(length: Int) = (0..length).map { rnd.nextInt(charPool.size) }.map { charPool[it] }.joinToString("")

    fun randomDate() = Date(rnd.nextLong())
}