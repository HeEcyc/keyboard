package dev.patrickgold.florisboard.ui

import android.util.Log
import dev.patrickgold.florisboard.background.view.keyboard.repository.BackgroundViewRepository
import dev.patrickgold.florisboard.ui.base.BaseViewModel

class TestViewModel : BaseViewModel() {
    private val bgRepository = BackgroundViewRepository

    fun attachThemeType(view: BackgroundViewRepository.BackgroundView) {
        Log.d("12345", view::class.java.simpleName)
        bgRepository.setNewBackgroundView(view)
    }
}
