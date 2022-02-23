package com.ioskey.iosboard.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ioskey.iosboard.data.KeyboardTheme

@Dao
interface ThemeDao {

    @Query("select * from keyboardtheme")
    fun getTheme(): List<KeyboardTheme>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTheme(KeyboardTheme: KeyboardTheme): Long

}
