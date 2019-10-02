package gr.blackswamp.core.util

object TestLog : ILog {
    override fun d(tag: String, message: String, throwable: Throwable?) {
        println("$tag : $message")
        throwable?.let {
            println(it.message)
            println(it.stackTrace)
        }
    }

    override fun v(tag: String, message: String, throwable: Throwable?) {
        println("$tag : $message")
        throwable?.let {
            println(it.message)
            println(it.stackTrace)
        }
    }

    override fun i(tag: String, message: String, throwable: Throwable?) {
        println("$tag : $message")
        throwable?.let {
            println(it.message)
            println(it.stackTrace)
        }
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        println("$tag : $message")
        throwable?.let {
            println(it.message)
            println(it.stackTrace)
        }
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        println("$tag : $message")
        throwable?.let {
            println(it.message)
            println(it.stackTrace)
        }
    }

    override fun wtf(tag: String, message: String, throwable: Throwable?) {
        println("$tag : $message")
        throwable?.let {
            println(it.message)
            println(it.stackTrace)
        }
    }
}