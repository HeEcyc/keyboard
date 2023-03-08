package com.gg.osto.ime.media.emoji

import com.gg.osto.ime.keyboard.Key
import com.gg.osto.ime.keyboard.KeyData
import com.gg.osto.ime.popup.PopupSet

class EmojiKey(override val data: KeyData) : Key(data) {
    var computedData: EmojiKeyData = EmojiKeyData(listOf())
        private set
    var computedPopups: PopupSet<KeyData> = PopupSet()
        private set

    companion object {
        val EMPTY = EmojiKey(EmojiKeyData.EMPTY)
    }

    fun dummyCompute() {
        computedData = data as? EmojiKeyData ?: computedData
        computedPopups = PopupSet(relevant = (data as? EmojiKeyData)?.popupList ?: listOf())
    }
}
