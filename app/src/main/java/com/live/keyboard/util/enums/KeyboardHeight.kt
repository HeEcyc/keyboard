package com.live.keyboard.util.enums

import com.live.keyboard.R

enum class KeyboardHeight(
    val displayName: Int,
    val mainKeyboardSizePercent: Float,
    val mainKeyboardLabelFontSizePercent: Float,
    val mainKeyboardDrawableSizePercent: Float
) {
    SHORT(
        R.string.keyboard_height_short,
        0.8f,
        1f,
        1f
    ),
    NORMAL(
        R.string.keyboard_height_normal,
        1f,
        1f,
        1f
    ),
    TALL(
        R.string.keyboard_height_tall,
        1f,
        1.25f,
        1.7f
    )
}
