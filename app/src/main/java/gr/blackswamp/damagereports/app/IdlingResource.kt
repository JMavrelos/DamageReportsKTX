package gr.blackswamp.damagereports.app


import androidx.test.espresso.idling.CountingIdlingResource

class IdlingResource {
    companion object {
        private const val RESOURCE = "GLOBAL"
    }

    var isLive: Boolean = true

    @JvmField
    val counting = CountingIdlingResource(RESOURCE)

    fun increment() {
        if (isLive)
            counting.increment()
    }

    fun decrement() {
        if (isLive && counting.isIdleNow) {
            counting.decrement()
        }
    }
}