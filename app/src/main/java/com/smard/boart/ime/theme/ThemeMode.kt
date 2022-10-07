package com.smard.boart.ime.theme

/**
 * Enum class which specifies all theme modes available. Used in the Settings to properly manage
 * different use cases when the day or night theme should be active.
 */
enum class ThemeMode {
    ALWAYS_DAY,
    ALWAYS_NIGHT,
    FOLLOW_SYSTEM,
    FOLLOW_TIME;

    companion object {
        fun fromString(string: String): ThemeMode {
            return valueOf(string.uppercase())
        }
    }
}
