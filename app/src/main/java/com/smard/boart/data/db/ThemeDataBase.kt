package com.smard.boart.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smard.boart.App
import com.smard.boart.data.DictionaryEntry
import com.smard.boart.data.KeyboardTheme


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
