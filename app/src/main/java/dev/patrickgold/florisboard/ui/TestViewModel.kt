package dev.patrickgold.florisboard.ui

import androidx.annotation.FontRes
import dev.patrickgold.florisboard.background.view.keyboard.repository.BackgroundViewRepository
import dev.patrickgold.florisboard.repository.PrefsReporitory
import dev.patrickgold.florisboard.ui.base.BaseViewModel

class TestViewModel : BaseViewModel() {

    fun attachThemeType(view: BackgroundViewRepository.BackgroundView) {
        BackgroundViewRepository.setNewBackgroundView(view)
    }

    fun setFont(@FontRes font: Int) {
        PrefsReporitory.fontFamilyRes = font
    }

    fun setColor(color: Int) {
        PrefsReporitory.keyColor = color
    }
}
