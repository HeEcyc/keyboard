package com.gg.osto.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gg.osto.App
import com.gg.osto.data.DictionaryEntry
import com.gg.osto.data.KeyboardTheme


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
