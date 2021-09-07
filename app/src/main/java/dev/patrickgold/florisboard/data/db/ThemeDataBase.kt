package dev.patrickgold.florisboard.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.patrickgold.florisboard.FlorisApplication
import dev.patrickgold.florisboard.data.KeyboardTheme


@Database(entities = [KeyboardTheme::class], version = 1)
abstract class ThemeDataBase : RoomDatabase() {

    abstract fun getThemesDao(): ThemeDao

    companion object {
        val dataBase = Room
            .databaseBuilder(FlorisApplication.instance, ThemeDataBase::class.java, "keyboard_themes")
            .allowMainThreadQueries()
            .build()
    }
}
