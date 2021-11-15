package com.keyboard.neon_keyboard.ui.glide.preferences.activity

import androidx.databinding.ObservableBoolean
import com.keyboard.neon_keyboard.repository.PrefsReporitory
import com.keyboard.neon_keyboard.ui.base.BaseViewModel

class GlideTypingPreferenceViewModel : BaseViewModel() {

    val enableGlideTyping = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)
    val showGestureTrail = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.showGestureTrail)
    val enableGestureCursorControl = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGestureCursorControl)

    init {
        observe(enableGlideTyping) { _, _ ->
            PrefsReporitory.Settings.GlideTyping.enableGlideTyping = enableGlideTyping.get()
        }
        observe(showGestureTrail) { _, _ ->
            PrefsReporitory.Settings.GlideTyping.showGestureTrail = showGestureTrail.get()
        }
        observe(enableGestureCursorControl) { _, _ ->
            PrefsReporitory.Settings.GlideTyping.enableGestureCursorControl = enableGestureCursorControl.get()
        }
    }

}
