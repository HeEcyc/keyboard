package com.cccomba.board.ime.text.key

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class KeyVariation(val value: Int) {
    @SerialName("all")
    ALL(0),
    @SerialName("email")
    EMAIL_ADDRESS(1),
    @SerialName("normal")
    NORMAL(2),
    @SerialName("password")
    PASSWORD(3),
    @SerialName("uri")
    URI(4);

    companion object {
        fun fromInt(int: Int) = values().firstOrNull { it.value == int } ?: ALL
    }

    fun toInt() = value
}
