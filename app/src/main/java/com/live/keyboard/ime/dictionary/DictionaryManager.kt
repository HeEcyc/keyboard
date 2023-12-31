package com.live.keyboard.ime.dictionary

import android.content.Context
import androidx.room.Room
import com.live.keyboard.common.FlorisLocale
import com.live.keyboard.ime.core.Preferences
import com.live.keyboard.ime.core.Subtype
import com.live.keyboard.ime.nlp.SuggestionList
import com.live.keyboard.ime.nlp.Word
import com.live.keyboard.util.enums.Language
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.lang.ref.WeakReference

/**
 * TODO: document
 */
class DictionaryManager private constructor(
    context: Context,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val applicationContext: WeakReference<Context> = WeakReference(context.applicationContext ?: context)
    private val prefs get() = Preferences.default()

    var dictionaryCache: TypedDictionary? = null

    private var florisUserDictionaryDatabase: FlorisUserDictionaryDatabase? = null
    private var systemUserDictionaryDatabase: SystemUserDictionaryDatabase? = null

    companion object {
        private var defaultInstance: DictionaryManager? = null

        fun init(applicationContext: Context): DictionaryManager {
            val instance = DictionaryManager(applicationContext)
            defaultInstance = instance
            return instance
        }

        fun default(): DictionaryManager {
            val instance = defaultInstance
            if (instance != null) {
                return instance
            } else {
                throw UninitializedPropertyAccessException(
                    "${DictionaryManager::class.simpleName} has not been initialized previously. Make sure to call init(applicationContext) before using default()."
                )
            }
        }
    }

    inline fun suggest(
        currentWord: Word,
        preceidingWords: List<Word>,
        subtype: Subtype,
        allowPossiblyOffensive: Boolean,
        maxSuggestionCount: Int,
        block: (suggestions: SuggestionList) -> Unit
    ) {
        val suggestions = SuggestionList.new(maxSuggestionCount)
        queryUserDictionary(currentWord, subtype.locale, suggestions)
        search(currentWord, suggestions)
        block(suggestions)
        suggestions.dispose()
    }

    fun search(currentWord: Word, suggestions: SuggestionList) {
        dictionaryCache?.search(currentWord, suggestions)
    }

    fun prepareDictionaries(subtype: Subtype) {
        val language = Language.from(subtype.locale.language)
        dictionaryCache = TypedDictionary(language, language == Language.EN)
    }

    fun queryUserDictionary(word: Word, locale: FlorisLocale, destSuggestionList: SuggestionList) {
        val florisDao = florisUserDictionaryDao()
        val systemDao = systemUserDictionaryDao()
        if (florisDao == null && systemDao == null) {
            return
        }
        if (prefs.dictionary.enableFlorisUserDictionary) {
            florisDao?.query(word, locale)?.let {
                for (entry in it) {
                    destSuggestionList.add(entry.word, entry.freq)
                }
            }
            florisDao?.queryShortcut(word, locale)?.let {
                for (entry in it) {
                    destSuggestionList.add(entry.word, entry.freq)
                }
            }
        }
        if (prefs.dictionary.enableSystemUserDictionary) {
            systemDao?.query(word, locale)?.let {
                for (entry in it) {
                    destSuggestionList.add(entry.word, entry.freq)
                }
            }
            systemDao?.queryShortcut(word, locale)?.let {
                for (entry in it) {
                    destSuggestionList.add(entry.word, entry.freq)
                }
            }
        }
    }

    fun spell(word: Word, locale: FlorisLocale): Boolean {
        val florisDao = florisUserDictionaryDao()
        val systemDao = systemUserDictionaryDao()
        if (florisDao == null && systemDao == null) {
            return false
        }
        var ret = false
        if (prefs.dictionary.enableFlorisUserDictionary) {
            ret = ret || florisDao?.queryExactFuzzyLocale(word, locale)?.isNotEmpty() ?: false
            ret = ret || florisDao?.queryShortcut(word, locale)?.isNotEmpty() ?: false
        }
        if (prefs.dictionary.enableSystemUserDictionary) {
            ret = ret || systemDao?.queryExactFuzzyLocale(word, locale)?.isNotEmpty() ?: false
            ret = ret || systemDao?.queryShortcut(word, locale)?.isNotEmpty() ?: false
        }
        return ret
    }

    @Synchronized
    fun florisUserDictionaryDao(): UserDictionaryDao? {
        return if (prefs.dictionary.enableFlorisUserDictionary) {
            florisUserDictionaryDatabase?.userDictionaryDao()
        } else {
            null
        }
    }

    @Synchronized
    fun florisUserDictionaryDatabase(): FlorisUserDictionaryDatabase? {
        return if (prefs.dictionary.enableFlorisUserDictionary) {
            florisUserDictionaryDatabase
        } else {
            null
        }
    }

    @Synchronized
    fun systemUserDictionaryDao(): UserDictionaryDao? {
        return if (prefs.dictionary.enableSystemUserDictionary) {
            systemUserDictionaryDatabase?.userDictionaryDao()
        } else {
            null
        }
    }

    @Synchronized
    fun systemUserDictionaryDatabase(): SystemUserDictionaryDatabase? {
        return if (prefs.dictionary.enableSystemUserDictionary) {
            systemUserDictionaryDatabase
        } else {
            null
        }
    }

    @Synchronized
    fun loadUserDictionariesIfNecessary() {
        val context = applicationContext.get() ?: return

        if (florisUserDictionaryDatabase == null && prefs.dictionary.enableFlorisUserDictionary) {
            florisUserDictionaryDatabase = Room.databaseBuilder(
                context,
                FlorisUserDictionaryDatabase::class.java,
                FlorisUserDictionaryDatabase.DB_FILE_NAME
            ).allowMainThreadQueries().build()
        }
        if (systemUserDictionaryDatabase == null && prefs.dictionary.enableSystemUserDictionary) {
            systemUserDictionaryDatabase = SystemUserDictionaryDatabase(context)
        }
    }

    @Synchronized
    fun unloadUserDictionariesIfNecessary() {
        if (florisUserDictionaryDatabase != null) {
            florisUserDictionaryDatabase?.close()
            florisUserDictionaryDatabase = null
        }
        if (systemUserDictionaryDatabase != null) {
            systemUserDictionaryDatabase = null
        }
    }


}
