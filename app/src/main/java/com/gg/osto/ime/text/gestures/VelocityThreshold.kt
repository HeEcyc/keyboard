package com.gg.osto.ime.text.gestures

/**
 * Enum for declaring the velocity thresholds for swipe gestures.
 */
enum class VelocityThreshold {
    VERY_SLOW,
    SLOW,
    NORMAL,
    FAST,
    VERY_FAST;

    companion object {
        fun fromString(string: String): VelocityThreshold {
            return valueOf(string.uppercase())
        }
    }

    override fun toString(): String {
        return super.toString().lowercase()
    }
}
