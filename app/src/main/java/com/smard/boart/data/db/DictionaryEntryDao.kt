package com.smard.boart.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.smard.boart.data.DictionaryEntry

@Dao
interface DictionaryEntryDao {

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
