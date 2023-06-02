package com.cccomba.board.ime.text.key

/**
 * Enum for the key hint modes.
 */
enum class KeyHintMode {
    DISABLED,
    ENABLED_HINT_PRIORITY,
    ENABLED_ACCENT_PRIORITY,
    ENABLED_SMART_PRIORITY;

    companion object {
        fun fromString(string: String): KeyHintMode {
            return valueOf(string.uppercase())
        }
    }

    override fun toString(): String {
        return super.toString().lowercase()
    }
}
