package com.cccomba.board.data

import androidx.room.Entity
import com.cccomba.board.util.enums.Language

@Entity(tableName = "dictionary_entries", primaryKeys = ["word", "languageCode"])
data class DictionaryEntry(
    var word: String = "",
    var languageCode: String = "",
    var frequency: Int = 0
) {
    fun getLanguage() = Language.valueOf(languageCode)
}
