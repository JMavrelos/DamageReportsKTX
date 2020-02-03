package gr.blackswamp.damagereports.data.prefs

enum class ThemeMode(val value: Int) {
    System(0),
    Dark(1),
    Light(2),
    Auto(3);

    companion object {
        fun read(int: Int): ThemeMode {
            return when (int) {
                1 -> Dark
                2 -> Light
                3 -> Auto
                else -> System
            }
        }
    }
}