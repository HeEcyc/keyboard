package com.live.keyboard.ui.preview.theme.activity

import androidx.databinding.ObservableField
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.ui.base.BaseViewModel

class ThemePreviewViewModel : BaseViewModel() {

    val theme = ObservableField<KeyboardTheme>()

    val currentBackgroundColor = ObservableField<String?>()
    val backgroundImage = ObservableField<String?>()

    fun setTheme(keyboardTheme: KeyboardTheme) {
        theme.set(keyboardTheme)
        backgroundImage.set(keyboardTheme.backgroundImagePath)
    }
}
