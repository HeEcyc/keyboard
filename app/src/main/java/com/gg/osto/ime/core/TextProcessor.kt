package com.gg.osto.ime.core

import com.gg.osto.common.FlorisLocale

@Suppress("RegExpRedundantEscape")
object TextProcessor {
    private val LATIN_BASIC_WORD_REGEX = """[_]*(([\p{L}\d\']+[_-]*[\p{L}\d\']+)|[\p{L}\d\']+)[_]*""".toRegex()

    private fun wordRegexFor(locale: FlorisLocale): Regex {
        return when (locale) {
            else -> LATIN_BASIC_WORD_REGEX
        }
    }

    fun detectWords(text: CharSequence, locale: FlorisLocale): Sequence<IntRange> {
        val regex = wordRegexFor(locale)
        return regex.findAll(text).map { it.range }
    }

    fun detectWords(text: CharSequence, start: Int, end: Int, locale: FlorisLocale): Sequence<IntRange> {
        val regex = wordRegexFor(locale)
        val tStart = start.coerceAtLeast(0)
        val tEnd = end.coerceAtMost(text.length)
        return regex.findAll(text.slice(tStart..tEnd)).map { it.range }
    }

    fun isWord(text: CharSequence, locale: FlorisLocale): Boolean {
        val regex = wordRegexFor(locale)
        return regex.matches(text)
    }
}
