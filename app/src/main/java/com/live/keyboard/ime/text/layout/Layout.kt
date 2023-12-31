package com.live.keyboard.ime.text.layout

import com.live.keyboard.ime.keyboard.AbstractKeyData
import com.live.keyboard.res.Asset
import com.live.keyboard.ime.text.key.*
import com.live.keyboard.ime.text.keyboard.TextKeyData
import kotlinx.serialization.Serializable

@Serializable
data class Layout(
    val type: LayoutType,
    override val name: String,
    override val label: String,
    override val authors: List<String>,
    val direction: String,
    val modifier: String? = null,
    val arrangement: List<List<AbstractKeyData>> = listOf()
) : Asset {
    companion object {
        val PRE_GENERATED_LOADING_KEYBOARD = Layout(
            type = LayoutType.CHARACTERS,
            name = "__loading_keyboard__",
            label = "__loading_keyboard__",
            authors = listOf(),
            direction = "ltr",
            arrangement = listOf(
                listOf(
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0)
                ),
                listOf(
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0)
                ),
                listOf(
                    TextKeyData(code = KeyCode.SHIFT, type = KeyType.MODIFIER, label = "shift"),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = KeyCode.DELETE, type = KeyType.ENTER_EDITING, label = "delete")
                ),
                listOf(
                    TextKeyData(code = KeyCode.VIEW_SYMBOLS, type = KeyType.SYSTEM_GUI, label = "view_symbols"),
                    TextKeyData(code = 0),
                    TextKeyData(code = 0),
                    TextKeyData(code = KeyCode.SPACE, label = "space"),
                    TextKeyData(code = 0),
                    TextKeyData(code = KeyCode.ENTER, type = KeyType.ENTER_EDITING, label = "enter")
                )
            )
        )
    }
}

@Serializable
data class LayoutMetaOnly(
    val type: LayoutType,
    override val name: String,
    override val label: String,
    override val authors: List<String>,
    val direction: String,
    val modifier: String? = null
) : Asset
