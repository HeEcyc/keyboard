package com.neonkeyboard.cool

import android.service.textservice.SpellCheckerService
import android.view.textservice.SentenceSuggestionsInfo
import android.view.textservice.SuggestionsInfo
import android.view.textservice.TextInfo
import com.neonkeyboard.cool.common.FlorisLocale
import com.neonkeyboard.cool.ime.core.Subtype
import com.neonkeyboard.cool.ime.core.SubtypeManager
import com.neonkeyboard.cool.ime.dictionary.DictionaryManager
import com.neonkeyboard.cool.ime.spelling.SpellingService
import kotlinx.coroutines.runBlocking

class NeonSpellCheckerService : SpellCheckerService() {
    companion object {
        private const val USE_FLORIS_SUBTYPES_LOCALE: String = "zz"
    }

    private val dictionaryManager get() = DictionaryManager.default()
    private val spellingService: SpellingService = SpellingService.globalInstance()
    private val subtypeManager get() = SubtypeManager.default()

    override fun onCreate() {
        super.onCreate()
        dictionaryManager.loadUserDictionariesIfNecessary()
    }

    override fun createSession(): Session {
        return FlorisSpellCheckerSession()
    }

    private inner class FlorisSpellCheckerSession : Session() {
        private var cachedSpellingLocale: FlorisLocale? = null

        override fun onCreate() {
            setupSpellingIfNecessary()
        }

        private fun setupSpellingIfNecessary() {
            val evaluatedLocale = when (locale) {
                null -> Subtype.DEFAULT.locale
                USE_FLORIS_SUBTYPES_LOCALE -> (subtypeManager.getActiveSubtype() ?: Subtype.DEFAULT).locale
                else -> FlorisLocale.from(locale)
            }

            if (evaluatedLocale != cachedSpellingLocale) {
                cachedSpellingLocale = evaluatedLocale
            }
        }

        private fun spellMultiple(
            spellingLocale: FlorisLocale,
            textInfos: Array<out TextInfo>,
            suggestionsLimit: Int
        ): Array<SuggestionsInfo> = runBlocking {
            val retInfos = Array(textInfos.size) { n ->
                val word = textInfos[n].text ?: ""
                spellingService.spellAsync(spellingLocale, word, suggestionsLimit)
            }
            Array(textInfos.size) { n ->
                retInfos[n].await().apply {
                    setCookieAndSequence(textInfos[n].cookie, textInfos[n].sequence)
                }
            }
        }

        override fun onGetSuggestions(textInfo: TextInfo?, suggestionsLimit: Int): SuggestionsInfo {
            textInfo?.text ?: return SpellingService.emptySuggestionsInfo()
            setupSpellingIfNecessary()
            val spellingLocale = cachedSpellingLocale ?: return SpellingService.emptySuggestionsInfo()

            return spellingService.spell(spellingLocale, textInfo.text, suggestionsLimit)
        }

        override fun onGetSuggestionsMultiple(
            textInfos: Array<out TextInfo>?,
            suggestionsLimit: Int,
            sequentialWords: Boolean
        ): Array<SuggestionsInfo> {
            textInfos ?: return emptyArray()
            setupSpellingIfNecessary()
            val spellingLocale = cachedSpellingLocale ?: return emptyArray()

            return spellMultiple(spellingLocale, textInfos, suggestionsLimit)
        }

        override fun onGetSentenceSuggestionsMultiple(
            textInfos: Array<out TextInfo>?,
            suggestionsLimit: Int
        ): Array<SentenceSuggestionsInfo> {
            // TODO: implement custom solution here instead of calling the default implementation
            return super.onGetSentenceSuggestionsMultiple(textInfos, suggestionsLimit)
        }

    }
}
