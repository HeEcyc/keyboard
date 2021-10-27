package com.live.keyboard.ime.dictionary

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.live.keyboard.FlorisApplication
import com.live.keyboard.data.db.ThemeDataBase
import com.live.keyboard.ime.nlp.SuggestionList
import com.live.keyboard.ime.nlp.Word
import com.live.keyboard.util.enums.Language
import com.live.keyboard.util.getFile
import java.lang.Exception

class TypedDictionary(val language: Language, readFromAssets: Boolean = true) {

    val wordsFromJSON: HashMap<String, Int> = HashMap()
    var words: HashMap<String, Int> = loadDictionary(language, readFromAssets)

    fun loadDictionary(language: Language, readFromAssets: Boolean) =
        if (readFromAssets) {
            FlorisApplication.instance.assets.open(language.dictionaryJSONFile)
        } else {
            language.dictionaryJSONFile.getFile().inputStream()
        }
            .bufferedReader()
            .use { reader -> reader.readText() }
            .let { mapJSONStringToHasMap(it, language.name) }

    private fun mapJSONStringToHasMap(json: String, languageCode: String): HashMap<String, Int> {
        val map = HashMap<String, Int>()
        try {
            map.putAll(Gson().fromJson(json, object : TypeToken<HashMap<String, Int>>() {}.type))
            wordsFromJSON.putAll(map)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        map.putAll(ThemeDataBase.dataBase
            .getDictionaryDao()
            .getFormattedEntriesForLanguage(languageCode)
            .map { it.word to it.frequency })
        return map
    }

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
