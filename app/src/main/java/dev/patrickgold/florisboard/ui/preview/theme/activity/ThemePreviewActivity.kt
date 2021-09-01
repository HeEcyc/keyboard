package dev.patrickgold.florisboard.ui.preview.theme.activity

import androidx.activity.viewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ThemePreviewActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity

class ThemePreviewActivity :
    BaseActivity<ThemePreviewViewModel, ThemePreviewActivityBinding>(R.layout.theme_preview_activity) {

    private val viewModel: ThemePreviewViewModel by viewModels()

    override fun setupUI() {

    }

    override fun provideViewModel() = viewModel
}
