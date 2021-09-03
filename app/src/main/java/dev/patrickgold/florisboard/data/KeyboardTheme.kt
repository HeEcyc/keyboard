package dev.patrickgold.florisboard.data

import java.io.Serializable

data class KeyboardTheme(
    val backgoundType: String,
    val backgroundColor: Int?,
    val backgroundImagePath: String?,
    val keyFont: Int,
    val keyColor: Int,
    val strokeType: Int?,
    val strokeColor: Int?
) : Serializable
