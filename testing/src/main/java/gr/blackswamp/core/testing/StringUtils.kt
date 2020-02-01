package gr.blackswamp.core.testing

import kotlin.random.Random


private val charPool: List<Char> by lazy { ('a'..'z') + ('A'..'Z') + ('0'..'9') }
fun randomString(length: Int): String {
    return (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}

fun randomStringList(number: Int, length: Int): List<String> {
    return (1..number).map { randomString(length) }
}
