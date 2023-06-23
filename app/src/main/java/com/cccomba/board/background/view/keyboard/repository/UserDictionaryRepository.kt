package com.cccomba.board.background.view.keyboard.repository

import com.cccomba.board.data.db.ThemeDataBase
import com.cccomba.board.ime.dictionary.DictionaryManager

object UserDictionaryRepository {

    private val nonCharacterNonDigit = "[[^\\p{L}]&&[\\D]]".toRegex()
    private val digitsOnly = "[\\d]+".toRegex()

    fun processText(text: String) {

        val dictionary = DictionaryManager.default().dictionaryCache ?: return

        text.split(nonCharacterNonDigit)
            .asSequence()
            .filterNot { it.matches(digitsOnly) }
            .filterNot { it.isBlank() }
            .forEach {
                ThemeDataBase.dataBase
                    .getDictionaryDao()
                    .incrementFrequencyOrInsertNewEntry(it, dictionary.language.name)
            }
    }

}