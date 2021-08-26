package dev.patrickgold.florisboard.ime.dictionary

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.patrickgold.florisboard.FlorisApplication
import dev.patrickgold.florisboard.ime.nlp.SuggestionList
import dev.patrickgold.florisboard.ime.nlp.Word

class TypedDictionary(dictionaryPath: String) {
    private var words: HashMap<String, Int> = loadDictionary(dictionaryPath)

    fun loadDictionary(path: String) = FlorisApplication.instance.assets
        .open(path)
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
