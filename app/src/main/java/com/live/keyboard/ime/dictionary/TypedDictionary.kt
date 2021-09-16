package com.live.keyboard.ime.dictionary

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.live.keyboard.FlorisApplication
import com.live.keyboard.ime.nlp.SuggestionList
import com.live.keyboard.ime.nlp.Word
import com.live.keyboard.util.getFile

class TypedDictionary(dictionaryPath: String, readFromAssets: Boolean = true) {
    private var words: HashMap<String, Int> = loadDictionary(dictionaryPath, readFromAssets)

    fun loadDictionary(path: String, readFromAssets: Boolean) =
        if (readFromAssets) {
            FlorisApplication.instance.assets.open(path)
        } else {
            path.getFile().inputStream()
        }
        .bufferedReader()
        .use { reader -> reader.readText() }
        .let(::mapJSONStringToHasMap)

    private fun mapJSONStringToHasMap(json: String): HashMap<String, Int> = Gson()
        .fromJson(json, object : TypeToken<HashMap<String, Int>>() {}.type)

    fun isInit() = words.isNotEmpty()

    fun search(currentWord: Word, suggestions: SuggestionList) {
        if (!isInit()) return
        words.forEach {
            val dictionaryWord = it.key
            val freq = it.value
            if (dictionaryWord.startsWith(currentWord, false))
                suggestions.add(dictionaryWord, freq)
        }
    }
}
