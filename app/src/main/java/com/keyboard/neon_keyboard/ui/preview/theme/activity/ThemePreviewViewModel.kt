package com.keyboard.neon_keyboard.ui.preview.theme.activity

import androidx.databinding.ObservableField
import com.keyboard.neon_keyboard.data.KeyboardTheme
import com.keyboard.neon_keyboard.ui.base.BaseViewModel

class ThemePreviewViewModel : BaseViewModel() {

    val theme = ObservableField<KeyboardTheme>()

    val currentBackgroundColor = ObservableField<String?>()
    val backgroundImage = ObservableField<String?>()

    fun setTheme(keyboardTheme: KeyboardTheme) {
        theme.set(keyboardTheme)
        backgroundImage.set(keyboardTheme.backgroundImagePath)
    }
}
