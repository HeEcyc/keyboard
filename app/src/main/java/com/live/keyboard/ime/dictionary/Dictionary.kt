package com.live.keyboard.ime.dictionary

import com.live.keyboard.res.Asset
import com.live.keyboard.ime.nlp.SuggestionList
import com.live.keyboard.ime.nlp.Word

/**
 * Standardized dictionary interface for interacting with dictionaries.
 */
interface Dictionary : Asset {
    /**
     * Gets token predictions based on the given [precedingTokens] and the [currentToken]. The
     * length of the returned list is limited to [maxSuggestionCount]. Note that the returned list
     * may at any time give back less items than [maxSuggestionCount] indicates.
     */
    fun getTokenPredictions(
        precedingTokens: List<Word>,
        currentToken: Word?,
        maxSuggestionCount: Int,
        allowPossiblyOffensive: Boolean,
        destSuggestionList: SuggestionList
    )

    fun getDate(): Long

    fun getVersion(): Int
}

interface MutableDictionary : Dictionary {
    fun trainTokenPredictions(
        precedingTokens: List<Word>,
        lastToken: Word
    )

    fun setDate(date: Int)

    fun setVersion(version: Int)
}
