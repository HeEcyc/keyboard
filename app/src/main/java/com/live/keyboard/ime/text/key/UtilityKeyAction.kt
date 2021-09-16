package com.live.keyboard.ime.text.key

/**
 * Enum for declaring the utility key actions.
 */
enum class UtilityKeyAction {
    SWITCH_TO_EMOJIS,
    SWITCH_LANGUAGE,
    SWITCH_KEYBOARD_APP,
    DYNAMIC_SWITCH_LANGUAGE_EMOJIS,
    DISABLED;

    companion object {
        fun fromString(string: String): UtilityKeyAction {
            return valueOf(string.uppercase())
        }
    }
}
