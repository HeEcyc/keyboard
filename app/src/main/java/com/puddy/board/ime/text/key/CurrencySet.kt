package com.puddy.board.ime.text.key

import com.puddy.board.ime.text.keyboard.TextKeyData
import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
class CurrencySet(
    val name: String,
    val label: String,
    private val slots: List<TextKeyData>
) {
    companion object {
        fun isCurrencySlot(keyCode: Int): Boolean {
            return when (keyCode) {
                KeyCode.CURRENCY_SLOT_1,
                KeyCode.CURRENCY_SLOT_2,
                KeyCode.CURRENCY_SLOT_3,
                KeyCode.CURRENCY_SLOT_4,
                KeyCode.CURRENCY_SLOT_5,
                KeyCode.CURRENCY_SLOT_6 -> true
                else -> false
            }
        }

        fun default(): CurrencySet = CurrencySet(
            name = "\$default",
            label = "Default",
            slots = listOf(
                TextKeyData(code =   36, label = "$"),
                TextKeyData(code =  162, label = "¢"),
                TextKeyData(code = 8364, label = "€"),
                TextKeyData(code =  163, label = "£"),
                TextKeyData(code =  165, label = "¥"),
                TextKeyData(code = 8369, label = "₱")
            )
        )
    }

    fun getSlot(keyCode: Int): TextKeyData? {
        val slot = abs(keyCode) - abs(KeyCode.CURRENCY_SLOT_1)
        return slots.getOrNull(slot)
    }

    override fun toString(): String {
        return "${CurrencySet::class.simpleName} { name=$name, label\"$label\", slots=$slots }"
    }
}
