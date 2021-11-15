package com.keyboard.neon_keyboard.util.enums

import com.keyboard.neon_keyboard.R

enum class KeyboardHeight(val displayName: Int, val height: String) {
    SHORT(R.string.keyboard_height_short, "short"),
    NORMAL(R.string.keyboard_height_normal, "normal"),
    TALL(R.string.keyboard_height_tall, "tall")
}
