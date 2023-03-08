package com.gg.osto.ime.text.gestures

/**
 * Enum for declaring the possible actions for swipe gestures.
 */
enum class SwipeAction {
    NO_ACTION,
    CYCLE_TO_PREVIOUS_KEYBOARD_MODE,
    CYCLE_TO_NEXT_KEYBOARD_MODE,
    DELETE_CHARACTERS_PRECISELY,
    DELETE_WORD,
    DELETE_WORDS_PRECISELY,
    HIDE_KEYBOARD,
    INSERT_SPACE,
    MOVE_CURSOR_UP,
    MOVE_CURSOR_DOWN,
    MOVE_CURSOR_LEFT,
    MOVE_CURSOR_RIGHT,
    MOVE_CURSOR_START_OF_LINE,
    MOVE_CURSOR_END_OF_LINE,
    MOVE_CURSOR_START_OF_PAGE,
    MOVE_CURSOR_END_OF_PAGE,
    REDO,
    SHIFT,
    SHOW_INPUT_METHOD_PICKER,
    SWITCH_TO_PREV_SUBTYPE,
    SWITCH_TO_NEXT_SUBTYPE,
    SWITCH_TO_CLIPBOARD_CONTEXT,
    SWITCH_TO_PREV_KEYBOARD,
    UNDO;

    companion object {
        fun fromString(string: String): SwipeAction {
            return valueOf(string.uppercase())
        }
    }

    override fun toString(): String {
        return super.toString().lowercase()
    }
}
