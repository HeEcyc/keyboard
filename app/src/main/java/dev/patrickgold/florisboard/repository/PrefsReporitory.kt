package dev.patrickgold.florisboard.repository

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import dev.patrickgold.florisboard.FlorisApplication

object PrefsReporitory {
    private val sharedPreferences by lazy { FlorisApplication.instance.getSharedPreferences("prefs", MODE_PRIVATE) }

    const val fontResKey = "font_res_key"
    const val keyColorKey = "key_color_key"
    const val isEnableGetsureKey = "is_enable_getsure_key"

    var fontFamilyRes: Int = -1
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

    var isEnableGetsure: Boolean = false
        get() = sharedPreferences.getBoolean(isEnableGetsureKey, false)
        set(value) {
            sharedPreferences.edit().putBoolean(keyColorKey, value).apply()
            field = value
        }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }
}
