package com.ioskey.iosboard.ui.preview.theme.activity

import androidx.databinding.ObservableField
import com.ioskey.iosboard.data.KeyboardTheme
import com.ioskey.iosboard.ui.base.BaseViewModel

class ThemePreviewViewModel : BaseViewModel() {

    val theme = ObservableField<KeyboardTheme>()

    val currentBackgroundColor = ObservableField<String?>()
    val backgroundImage = ObservableField<String?>()

    fun setTheme(keyboardTheme: KeyboardTheme) {
        theme.set(keyboardTheme)
        backgroundImage.set(keyboardTheme.backgroundImagePath)
    }
}
