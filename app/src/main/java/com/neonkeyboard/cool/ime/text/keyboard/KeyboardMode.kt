package com.neonkeyboard.cool.ime.text.keyboard

enum class KeyboardMode(val value: Int) {
    CHARACTERS(0),
    EDITING(1),
    SYMBOLS(2),
    SYMBOLS2(3),
    NUMERIC(4),
    NUMERIC_ADVANCED(5),
    PHONE(6),
    PHONE2(7),
    SMARTBAR_CLIPBOARD_CURSOR_ROW(8),
    SMARTBAR_NUMBER_ROW(9);

    companion object {
        fun fromInt(int: Int) = values().firstOrNull { it.value == int } ?: CHARACTERS
    }

    fun toInt() = value
}
