package com.puddy.board.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.puddy.board.App
import com.puddy.board.data.DictionaryEntry
import com.puddy.board.data.KeyboardTheme


@Database(entities = [KeyboardTheme::class, DictionaryEntry::class], version = 1)
abstract class ThemeDataBase : RoomDatabase() {

    abstract fun getThemesDao(): ThemeDao

    abstract fun getDictionaryDao(): DictionaryEntryDao

    companion object {
        val dataBase = Room
            .databaseBuilder(App.instance, ThemeDataBase::class.java, "keyboard_themes")
            .allowMainThreadQueries()
            .build()
    }
}
