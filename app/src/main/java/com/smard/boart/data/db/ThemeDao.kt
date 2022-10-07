package com.smard.boart.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smard.boart.data.KeyboardTheme

@Dao
interface ThemeDao {

    @Query("select * from keyboardtheme")
    fun getTheme(): List<KeyboardTheme>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTheme(KeyboardTheme: KeyboardTheme): Long

}
