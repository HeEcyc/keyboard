package com.cccomba.board.ime.media.emoji

import com.cccomba.board.ime.keyboard.Key
import com.cccomba.board.ime.keyboard.KeyData
import com.cccomba.board.ime.popup.PopupSet

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
