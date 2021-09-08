package dev.patrickgold.florisboard.ui.preview.theme.activity

import androidx.databinding.ObservableField
import dev.patrickgold.florisboard.background.view.keyboard.repository.BackgroundViewRepository
import dev.patrickgold.florisboard.data.KeyboardTheme
import dev.patrickgold.florisboard.ui.base.BaseViewModel

class ThemePreviewViewModel : BaseViewModel() {
    val currentBackgroundColor = ObservableField(KeyboardTheme().backgroundColor)
    val backgroundView = ObservableField<BackgroundViewRepository.BackgroundView>()


}
