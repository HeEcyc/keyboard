package dev.patrickgold.florisboard.background.view.keyboard.repository

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import dev.patrickgold.florisboard.FlorisApplication

object PrefsReporitory {
    private val sharedPreferences by lazy { FlorisApplication.instance.getSharedPreferences("prefs", MODE_PRIVATE) }
    private const val fontResKey = "font_res_key"
    private const val keyColorKey = "key_color_key"

    var fontFamilyRes: Int = 1
        get() = sharedPreferences.getInt(fontResKey, -1)
        set(value) {
            sharedPreferences.edit().putInt(fontResKey, value).apply()
            field = value
        }

    var keyColor: Int = Color.BLACK
        get() = sharedPreferences.getInt(keyColorKey, Color.BLACK)
        set(value) {
            sharedPreferences.edit().putInt(keyColorKey, value).apply()
            field = value
        }
}
