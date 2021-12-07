package com.neonkeyboard.cool.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.neonkeyboard.cool.NeonApplication
import com.neonkeyboard.cool.data.DictionaryEntry
import com.neonkeyboard.cool.data.KeyboardTheme


@Database(entities = [KeyboardTheme::class, DictionaryEntry::class], version = 1)
abstract class ThemeDataBase : RoomDatabase() {

    abstract fun getThemesDao(): ThemeDao

    abstract fun getDictionaryDao(): DictionaryEntryDao

    companion object {
        val dataBase = Room
            .databaseBuilder(NeonApplication.instance, ThemeDataBase::class.java, "keyboard_themes")
            .allowMainThreadQueries()
            .build()
    }
}
