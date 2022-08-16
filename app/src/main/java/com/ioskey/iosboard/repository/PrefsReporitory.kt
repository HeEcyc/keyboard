package com.ioskey.iosboard.repository

import android.content.Context.MODE_PRIVATE
import androidx.databinding.ObservableField
import android.content.SharedPreferences
import com.google.gson.Gson
import com.ioskey.iosboard.NeonApplication
import com.ioskey.iosboard.background.view.keyboard.repository.BottomRightCharacterRepository
import com.ioskey.iosboard.data.KeyboardTheme
import com.ioskey.iosboard.ime.core.Preferences
import com.ioskey.iosboard.ime.core.SubtypeManager
import com.ioskey.iosboard.ime.text.gestures.SwipeAction
import com.ioskey.iosboard.util.enums.KeyboardHeight
import com.ioskey.iosboard.util.enums.Language
import com.ioskey.iosboard.util.enums.LanguageChange
import com.ioskey.iosboard.util.enums.OneHandedMode
import kotlin.reflect.KProperty

typealias L = Language

object PrefsReporitory {
    fun subscribeToPrefsChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private val sharedPreferences by lazy { NeonApplication.instance.getSharedPreferences("prefs", MODE_PRIVATE) }

    const val keyboadThemeKey = "keyboard_theme_key"
    private const val showStartSettingsKey = "show_start_settings_key"
    private const val isFirstLaunchKey = "is_first_launch_key"
    private const val KEY_FIRST_LAUNCH_MILLIS = "key_first_launch_millis"
    private const val KEY_SENT_FIRST_NOTIFICATION = "sent_first_notification"

    var showStartSettings: Boolean = false
        get() = sharedPreferences.getBoolean(showStartSettingsKey, false)
        set(value) {
            sharedPreferences.edit().putBoolean(showStartSettingsKey, value).apply()
            field = value
        }

    var keyboardTheme: KeyboardTheme? = null
        get() = Gson().fromJson(sharedPreferences.getString(keyboadThemeKey, null), KeyboardTheme::class.java)
        set(value) {
            if (value != null) sharedPreferences.edit().putString(keyboadThemeKey, Gson().toJson(value)).apply()
            field = value
        }

    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean(isFirstLaunchKey, true)
        set(value) = sharedPreferences.edit().putBoolean(isFirstLaunchKey, value).apply()

    var firstLaunchMillis: Long
        get() = sharedPreferences.getLong(KEY_FIRST_LAUNCH_MILLIS, -1)
        set(value) = sharedPreferences.edit().putLong(KEY_FIRST_LAUNCH_MILLIS, value).apply()

    var sentFirstNotification: Boolean
        get() = sharedPreferences.getBoolean(KEY_SENT_FIRST_NOTIFICATION, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SENT_FIRST_NOTIFICATION, value).apply()

    object Settings {
        private const val showEmojiKey = "show_emoji"
        private const val tipsKey = "tips"
        private const val keyboardSwipeKey = "keyboard_swipe"
        private const val showNumberRowKey = "show_number_row"
        private const val oneHandedModeKey = "one_handed_mode"
        private const val keyboardHeightKey = "keyboard_height"
        private const val languageChangeKey = "language_change"
        private const val specialSymbolKey = "special_symbol"
        private const val minimumSwipeSpeedKey = "minimum_swipe_speed"

        object Language {
            private const val languageKey = "language_"

            operator fun getValue(language: Any?, property: KProperty<*>): Boolean {
                if (language !is L) return false
                val isDefaultLanguage = language == L.EN
                return sharedPreferences.getBoolean(languageKey + language.name, isDefaultLanguage)
            }

            operator fun setValue(language: Any?, property: KProperty<*>, isSelected: Boolean) {
                if (language !is L) return
                SubtypeManager.default()
                    .apply { language.let(if (isSelected) ::addSubtypeForLanguage else ::removeSubtypeForLanguage) }
                sharedPreferences.edit().putBoolean(languageKey + language.name, isSelected).apply()
            }
        }

        object GlideTyping {
            private const val enableGlideTypingKey = "enable_glide_typing"
            private const val showGestureTrailKey = "show_gesture_trail"
            private const val enableGestureCursorControlKey = "enable_gesture_cursor_control"

            var enableGlideTyping: Boolean
                get() = sharedPreferences.getBoolean(enableGlideTypingKey, false)
                set(value) {
                    if (value) keyboardSwipe = false
                    sharedPreferences.edit().putBoolean(enableGlideTypingKey, value).apply()
                    Preferences.default().glide.enabled = value
                }

            var showGestureTrail: Boolean
                get() = sharedPreferences.getBoolean(showGestureTrailKey, true)
                set(value) {
                    sharedPreferences.edit().putBoolean(showGestureTrailKey, value).apply()
                    Preferences.default().glide.showTrail = value
                }

            var enableGestureCursorControl: Boolean
                get() = sharedPreferences.getBoolean(enableGestureCursorControlKey, true)
                set(value) {
                    if (value) languageChange = LanguageChange.SPECIAL_BUTTON
                    sharedPreferences.edit().putBoolean(enableGestureCursorControlKey, value).apply()
                }
        }

        var showEmoji: Boolean
            get() = sharedPreferences.getBoolean(showEmojiKey, true)
            set(value) = sharedPreferences.edit().putBoolean(showEmojiKey, value).apply()

        var tips: Boolean
            get() = sharedPreferences.getBoolean(tipsKey, true)
            set(value) = sharedPreferences.edit().putBoolean(tipsKey, value).apply()

        var keyboardSwipe: Boolean
            get() = sharedPreferences.getBoolean(keyboardSwipeKey, true)
            set(value) {
                if (value) GlideTyping.enableGlideTyping = false
                sharedPreferences.edit().putBoolean(keyboardSwipeKey, value).apply()
            }

        var showNumberRow: Boolean
            get() = sharedPreferences.getBoolean(showNumberRowKey, true)
            set(value) = sharedPreferences.edit().putBoolean(showNumberRowKey, value).apply()

        var oneHandedMode: OneHandedMode
            get() = OneHandedMode.valueOf(sharedPreferences.getString(oneHandedModeKey, OneHandedMode.OFF.name)!!)
            set(value) {
                if (oneHandedMode == OneHandedMode.OFF && value != OneHandedMode.OFF) keyboardHeight =
                    KeyboardHeight.NORMAL
                sharedPreferences.edit().putString(oneHandedModeKey, value.name).apply()
                oneHandedModeObservable.set(oneHandedMode)
            }
        val oneHandedModeObservable = ObservableField(oneHandedMode)

        var keyboardHeight: KeyboardHeight
            get() = KeyboardHeight.valueOf(sharedPreferences.getString(keyboardHeightKey, KeyboardHeight.NORMAL.name)!!)
            set(value) {
                if (keyboardHeight == KeyboardHeight.NORMAL && value != KeyboardHeight.NORMAL) oneHandedMode =
                    OneHandedMode.OFF
                sharedPreferences.edit().putString(keyboardHeightKey, value.name).apply()
                Preferences.default().keyboard.heightFactor = value.height
            }

        var languageChange: LanguageChange
            get() = LanguageChange.valueOf(
                sharedPreferences.getString(
                    languageChangeKey,
                    LanguageChange.SPECIAL_BUTTON.name
                )!!
            )
            set(value) {
                if (value == LanguageChange.SWIPE_THROUGH_SPACE) GlideTyping.enableGestureCursorControl = false
                sharedPreferences.edit().putString(languageChangeKey, value.name).apply()
                Preferences.default().gestures.apply {
                    spaceBarSwipeLeft =
                        (if (value == LanguageChange.SPECIAL_BUTTON) SwipeAction.MOVE_CURSOR_LEFT else SwipeAction.SWITCH_TO_PREV_SUBTYPE)
                    spaceBarSwipeRight =
                        (if (value == LanguageChange.SPECIAL_BUTTON) SwipeAction.MOVE_CURSOR_RIGHT else SwipeAction.SWITCH_TO_NEXT_SUBTYPE)
                }
            }

        var specialSymbol: Int
            get() = sharedPreferences.getInt(
                specialSymbolKey,
                BottomRightCharacterRepository.defaultBottomRightCharacter.first
            )
            set(value) = sharedPreferences.edit().putInt(specialSymbolKey, value).apply()

        var minimumSwipeSpeed: Int
            get() = sharedPreferences!!.getInt(minimumSwipeSpeedKey, 1750)
            set(value) = sharedPreferences!!.edit().putInt(minimumSwipeSpeedKey, value).apply()

    }

}
