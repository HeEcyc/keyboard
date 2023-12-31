package com.live.keyboard.background.view.keyboard.repository

import com.live.keyboard.data.db.ThemeDataBase
import com.live.keyboard.ime.dictionary.DictionaryManager

object UserDictionaryRepository {

    private val nonCharacterNonDigit = "[[^\\p{L}]&&[\\D]]".toRegex()
    private val digitsOnly = "[\\d]+".toRegex()

    fun processText(text: String) {
        val dictionary = DictionaryManager.default().dictionaryCache ?: return
        text.split(nonCharacterNonDigit)
            .asSequence()
            .filterNot { it.matches(digitsOnly) }
            .filterNot { it.isBlank()}
            .filterNot { it in dictionary.wordsFromJSON.keys }
            .forEach {
                ThemeDataBase.dataBase.getDictionaryDao().incrementFrequencyOrInsertNewEntry(it, dictionary.language.name)
            }
    }

}
