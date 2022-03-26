package com.ioskey.iosboard.ui.guid.activity.second

import androidx.databinding.ObservableBoolean
import com.ioskey.iosboard.repository.PrefsReporitory
import com.ioskey.iosboard.ui.base.BaseViewModel

class GuidSettingsViewModel : BaseViewModel() {

    val isGlideTypingOn = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)
    val isKeyboardSwipeOn = ObservableBoolean(PrefsReporitory.Settings.keyboardSwipe)

    fun onGlideTypingClick(): Boolean {
        if (isGlideTypingOn.get()) return false
        PrefsReporitory.Settings.GlideTyping.enableGlideTyping = true
        updateUI()
        return true
    }

    fun onKeyboardSwipeClick(): Boolean {
        if (isKeyboardSwipeOn.get()) return false
        PrefsReporitory.Settings.keyboardSwipe = true
        updateUI()
        return true
    }

    private fun updateUI() {
        isGlideTypingOn.set(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)
        isKeyboardSwipeOn.set(PrefsReporitory.Settings.keyboardSwipe)
    }

}
