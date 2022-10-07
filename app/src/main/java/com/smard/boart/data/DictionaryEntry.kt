package com.smard.boart.data

import androidx.room.Entity
import com.smard.boart.util.enums.Language

@Entity(tableName = "dictionary_entries", primaryKeys = ["word", "languageCode"])
data class DictionaryEntry(
    var word: String = "",
    var languageCode: String = "",
    var frequency: Int = 0
) {
    fun getLanguage() = Language.valueOf(languageCode)
}
