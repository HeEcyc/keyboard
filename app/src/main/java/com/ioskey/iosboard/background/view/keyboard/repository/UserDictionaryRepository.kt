package com.ioskey.iosboard.background.view.keyboard.repository

import com.ioskey.iosboard.data.db.ThemeDataBase
import com.ioskey.iosboard.ime.dictionary.DictionaryManager

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
