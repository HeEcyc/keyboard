package dev.patrickgold.florisboard.repository

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import dev.patrickgold.florisboard.FlorisApplication
import dev.patrickgold.florisboard.background.view.keyboard.repository.BottomRightCharacterRepository
import dev.patrickgold.florisboard.util.enums.KeyboardHeight
import dev.patrickgold.florisboard.util.enums.Language
import dev.patrickgold.florisboard.util.enums.LanguageChange
import dev.patrickgold.florisboard.util.enums.OneHandedMode
import kotlin.reflect.KProperty

typealias L = Language

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

    object Settings {
        private const val showEmojiKey = "show_emoji"
        private const val tipsKey = "tips"
        private const val keyboardSwipeKey = "keyboard_swipe"
        private const val showNumberRowKey = "show_number_row"
        private const val oneHandedModeKey = "one_handed_mode"
        private const val keyboardHeightKey = "keyboard_height"
        private const val languageChangeKey = "language_change"
        private const val specialSymbolKey = "special_symbol"

        object Language {
            private const val languageKey = "language_"

            operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
                if (thisRef !is L) return false
                val isDefaultLanguage = thisRef == L.EN
                return sharedPreferences.getBoolean(languageKey + thisRef.name, isDefaultLanguage)
            }

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
                if (thisRef !is L) return
                sharedPreferences.edit().putBoolean(languageKey + thisRef.name, value).apply()
            }
        }

        object GlideTyping {
            private const val enableGlideTypingKey = "enable_glide_typing"
            private const val showGestureTrailKey = "show_gesture_trail"
            private const val enableGestureCursorControlKey = "enable_gesture_cursor_control"

            var enableGlideTyping: Boolean
                get() = sharedPreferences.getBoolean(enableGlideTypingKey, true)
                set(value) = sharedPreferences.edit().putBoolean(enableGlideTypingKey, value).apply()

            var showGestureTrail: Boolean
                get() = sharedPreferences.getBoolean(showGestureTrailKey, true)
                set(value) = sharedPreferences.edit().putBoolean(showGestureTrailKey, value).apply()

            var enableGestureCursorControl: Boolean
                get() = sharedPreferences.getBoolean(enableGestureCursorControlKey, true)
                set(value) = sharedPreferences.edit().putBoolean(enableGestureCursorControlKey, value).apply()
        }

        var showEmoji: Boolean
            get() = sharedPreferences.getBoolean(showEmojiKey, true)
            set(value) = sharedPreferences.edit().putBoolean(showEmojiKey, value).apply()

        var tips: Boolean
            get() = sharedPreferences.getBoolean(tipsKey, true)
            set(value) = sharedPreferences.edit().putBoolean(tipsKey, value).apply()

        var keyboardSwipe: Boolean
            get() = sharedPreferences.getBoolean(keyboardSwipeKey, true)
            set(value) = sharedPreferences.edit().putBoolean(keyboardSwipeKey, value).apply()

        var showNumberRow: Boolean
            get() = sharedPreferences.getBoolean(showNumberRowKey, true)
            set(value) = sharedPreferences.edit().putBoolean(showNumberRowKey, value).apply()

        var oneHandedMode: OneHandedMode
            get() = OneHandedMode.valueOf(sharedPreferences.getString(oneHandedModeKey, OneHandedMode.OFF.name)!!)
            set(value) = sharedPreferences.edit().putString(oneHandedModeKey, value.name).apply()

        var keyboardHeight: KeyboardHeight
            get() = KeyboardHeight.valueOf(sharedPreferences.getString(keyboardHeightKey, KeyboardHeight.NORMAL.name)!!)
            set(value) = sharedPreferences.edit().putString(keyboardHeightKey, value.name).apply()

        var languageChange: LanguageChange
            get() = LanguageChange.valueOf(sharedPreferences.getString(languageChangeKey, LanguageChange.SWIPE_THROUGH_SPACE.name)!!)
            set(value) = sharedPreferences.edit().putString(languageChangeKey, value.name).apply()

        var specialSymbol: Int
            get() = sharedPreferences.getInt(specialSymbolKey, BottomRightCharacterRepository.defaultBottomRightCharacter.first)
            set(value) = sharedPreferences.edit().putInt(specialSymbolKey, value).apply()

    }

}
