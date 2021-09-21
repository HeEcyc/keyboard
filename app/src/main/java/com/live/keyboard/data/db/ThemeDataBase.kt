package com.live.keyboard.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.live.keyboard.FlorisApplication
import com.live.keyboard.data.DictionaryEntry
import com.live.keyboard.data.KeyboardTheme


@Database(entities = [KeyboardTheme::class, DictionaryEntry::class], version = 1)
abstract class ThemeDataBase : RoomDatabase() {

    abstract fun getThemesDao(): ThemeDao

    abstract fun getDictionaryDao(): DictionaryEntryDao

    companion object {
        val dataBase = Room
            .databaseBuilder(FlorisApplication.instance, ThemeDataBase::class.java, "keyboard_themes")
            .allowMainThreadQueries()
            .build()
    }
}
