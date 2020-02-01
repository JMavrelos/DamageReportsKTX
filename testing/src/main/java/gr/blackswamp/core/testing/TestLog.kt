package gr.blackswamp.core.testing

import gr.blackswamp.core.logging.ILog

object TestLog : ILog {
    override fun d(tag: String, message: String, throwable: Throwable?) {
        println("Debug $tag : $message ${throwable?.let { "($it)" } ?: ""}")
    }

    override fun v(tag: String, message: String, throwable: Throwable?) {
        println("Verbose $tag : $message ${throwable?.let { "($it)" } ?: ""}")
    }

    override fun i(tag: String, message: String, throwable: Throwable?) {
        println("Info $tag : $message ${throwable?.let { "($it)" } ?: ""}")
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        println("Warn $tag : $message ${throwable?.let { "($it)" } ?: ""}")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        println("Error $tag : $message ${throwable?.let { "($it)" } ?: ""}")
    }

    override fun wtf(tag: String, message: String, throwable: Throwable?) {
        println("WTF $tag : $message ${throwable?.let { "($it)" } ?: ""}")
    }
}