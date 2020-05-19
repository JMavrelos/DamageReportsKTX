package gr.blackswamp.core.util

fun String.toThrowable(): Throwable = Throwable(this)