package com.live.keyboard.ime.text.gestures

/**
 * Enum for declaring the distance thresholds for swipe gestures.
 */
enum class DistanceThreshold {
    VERY_SHORT,
    SHORT,
    NORMAL,
    LONG,
    VERY_LONG;

    companion object {
        fun fromString(string: String): DistanceThreshold {
            return valueOf(string.uppercase())
        }
    }

    override fun toString(): String {
        return super.toString().lowercase()
    }
}
