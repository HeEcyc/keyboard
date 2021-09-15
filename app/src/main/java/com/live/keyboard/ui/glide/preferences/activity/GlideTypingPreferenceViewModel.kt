package com.live.keyboard.ui.glide.preferences.activity

import androidx.databinding.ObservableBoolean
import com.live.keyboard.repository.PrefsReporitory
import com.live.keyboard.ui.base.BaseViewModel

class GlideTypingPreferenceViewModel : BaseViewModel() {

    val enableGlideTyping = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)
    val showGestureTrail = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.showGestureTrail)
    val enableGestureCursorControl = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGestureCursorControl)

    init {
        observe(enableGlideTyping) { _, _ ->
            PrefsReporitory.Settings.keyboardSwipe = !enableGlideTyping.get()
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
