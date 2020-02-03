package gr.blackswamp.damagereports.data.prefs

enum class ThemeSetting(val value: Int) {
    System(0),
    Dark(1),
    Light(2),
    Auto(3);

    companion object {
        fun fromValue(value: Int): ThemeSetting {
            return when (value) {
                1 -> Dark
                2 -> Light
                3 -> Auto
                else -> System
            }

        }
    }

}