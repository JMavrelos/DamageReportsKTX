package gr.blackswamp.core.logging

import android.util.Log
import gr.blackswamp.core.BuildConfig

object AppLog : ILog {
    override fun d(tag: String, message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG)
            throwable?.let { Log.d(tag, message, throwable) } ?: Log.v(tag, message)
    }

    override fun v(tag: String, message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            throwable?.let { Log.v(tag, message, throwable) } ?: Log.v(tag, message)
        }
    }

    override fun i(tag: String, message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            throwable?.let { Log.i(tag, message, throwable) } ?: Log.i(tag, message)
        }
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            throwable?.let { Log.w(tag, message, throwable) } ?: Log.w(tag, message)
        }
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            throwable?.let { Log.e(tag, message, throwable) } ?: Log.e(tag, message)
        }
    }

    override fun wtf(tag: String, message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            throwable?.let { Log.wtf(tag, message, throwable) } ?: Log.wtf(tag, message)
        }
    }

}