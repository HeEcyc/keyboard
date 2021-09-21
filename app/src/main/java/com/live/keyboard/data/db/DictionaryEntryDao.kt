package com.live.keyboard.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.live.keyboard.data.DictionaryEntry

@Dao
interface DictionaryEntryDao {

    @Query("SELECT * FROM dictionary_entries WHERE languageCode = :languageCode")
    fun getEntriesForLanguage(languageCode: String): List<DictionaryEntry>

    @Transaction
    fun getFormattedEntriesForLanguage(languageCode: String): List<DictionaryEntry> {
        val entries = getEntriesForLanguage(languageCode)
        return if (entries.isNotEmpty()) {
            val maxFrequency = entries.maxOf { it.frequency }
            entries.onEach { it.frequency = (it.frequency * 255 / maxFrequency).takeIf { it >= 1 } ?: 1 }
        } else entries
    }

    @Query("SELECT count(*) FROM dictionary_entries WHERE word = :word AND languageCode = :languageCode")
    fun hasEntry(word: String, languageCode: String): Int

    @Insert
    fun insertNewEntry(entry: DictionaryEntry)

    @Query("""
        UPDATE dictionary_entries
        SET frequency = (
            SELECT (frequency + 1)
            FROM dictionary_entries
            WHERE word = :word AND languageCode = :languageCode
        )
        WHERE word = :word AND languageCode = :languageCode
    """)
    fun incrementFrequency(word: String, languageCode: String)

    @Transaction
    fun incrementFrequencyOrInsertNewEntry(word: String, languageCode: String) {
        if (hasEntry(word, languageCode) == 0)
            insertNewEntry(DictionaryEntry(word, languageCode, 1))
        else
            incrementFrequency(word, languageCode)
    }

}
