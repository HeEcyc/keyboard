package com.neonkeyboard.cool.ui.preview.theme.activity

import androidx.databinding.ObservableField
import com.neonkeyboard.cool.data.KeyboardTheme
import com.neonkeyboard.cool.ui.base.BaseViewModel

class ThemePreviewViewModel : BaseViewModel() {

    val theme = ObservableField<KeyboardTheme>()

    val currentBackgroundColor = ObservableField<String?>()
    val backgroundImage = ObservableField<String?>()

    fun setTheme(keyboardTheme: KeyboardTheme) {
        theme.set(keyboardTheme)
        backgroundImage.set(keyboardTheme.backgroundImagePath)
    }
}
