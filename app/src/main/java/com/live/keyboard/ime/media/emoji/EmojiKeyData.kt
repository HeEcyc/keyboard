package com.live.keyboard.ime.media.emoji

import com.live.keyboard.ime.keyboard.AbstractKeyData
import com.live.keyboard.ime.keyboard.ComputingEvaluator
import com.live.keyboard.ime.keyboard.KeyData
import com.live.keyboard.ime.popup.PopupSet
import com.live.keyboard.ime.text.key.KeyCode
import com.live.keyboard.ime.text.key.KeyType
import com.live.keyboard.ime.text.keyboard.TextKeyData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data class for a single emoji (with possible emoji variants in [popup]). The JSON class identifier for this selector
 * is `emoji_key`.
 *
 * @property codePoints The code points of the emoji.
 * @property asString The name of the emoji.
 * @property popup List of possible variants of the emoji.
 */
@Serializable
@SerialName("emoji_key")
class EmojiKeyData(
    val codePoints: List<Int>,
    override var label: String = "",
    val popupList: MutableList<EmojiKeyData> = mutableListOf()
) : KeyData {
    @Transient override var type: KeyType = KeyType.CHARACTER
    @Transient override var code: Int = KeyCode.UNSPECIFIED
    @Transient override var groupId: Int = KeyData.GROUP_DEFAULT
    @Transient override var popup: PopupSet<AbstractKeyData> = PopupSet()

    override fun compute(evaluator: ComputingEvaluator): TextKeyData? {
        return null
    }

    private var string: String? = null

    override fun asString(isForDisplay: Boolean): String {
        if (string == null) {
            string = StringBuilder().run {
                for (codePoint in codePoints) {
                    append(Character.toChars(codePoint))
                }
                toString()
            }
        }
        return string!!
    }

    companion object {
        val EMPTY = EmojiKeyData(listOf())
    }

    override fun toString(): String {
        return "${EmojiKeyData::class.simpleName}"// { code=$code label=\"$label\" }"
    }
}
