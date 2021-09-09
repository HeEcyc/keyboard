package dev.patrickgold.florisboard.ui.preview.theme.activity

import androidx.databinding.ObservableField
import dev.patrickgold.florisboard.data.KeyboardTheme
import dev.patrickgold.florisboard.ui.base.BaseViewModel

class ThemePreviewViewModel : BaseViewModel() {

    val theme = ObservableField<KeyboardTheme>()

    val currentBackgroundColor = ObservableField<String?>()
    val backgroundImage = ObservableField<String?>()

    fun setTheme(keyboardTheme: KeyboardTheme) {
        theme.set(keyboardTheme)
        backgroundImage.set(keyboardTheme.backgroundImagePath)
    }
}
