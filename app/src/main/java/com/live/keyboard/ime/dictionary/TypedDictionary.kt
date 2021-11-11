package com.live.keyboard.ime.dictionary

import com.live.keyboard.ime.nlp.Word
import com.live.keyboard.util.enums.Language

class TypedDictionary(val language: Language, readFromAssets: Boolean = true) {

    val wordsFromJSON: HashMap<String, Int> = HashMap()
    var words: HashMap<String, Int> = hashMapOf()

    fun isInit() = words.isNotEmpty()

    fun search(currentWord: Word, suggestions: MutableList<Word>) {
        if (!isInit()) return
        val tempMap = mutableMapOf<Word, Int>()

        words.forEach {
            val dictionaryWord = it.key
            val freq = it.value
            if (dictionaryWord.startsWith(currentWord, true)) {
                tempMap[dictionaryWord] = freq
            }
        }
        tempMap.toList().sortedBy { (_, value) -> -value }.toMap()
            .keys.toList().let(suggestions::addAll)
    }
}
