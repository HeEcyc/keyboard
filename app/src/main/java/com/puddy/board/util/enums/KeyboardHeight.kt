package com.puddy.board.util.enums

import com.puddy.board.R

enum class KeyboardHeight(val displayName: Int, val height: String) {
    SHORT(R.string.keyboard_height_short, "short"),
    NORMAL(R.string.keyboard_height_normal, "normal"),
    TALL(R.string.keyboard_height_tall, "tall")
}
