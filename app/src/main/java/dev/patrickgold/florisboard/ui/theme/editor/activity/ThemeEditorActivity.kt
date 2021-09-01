package dev.patrickgold.florisboard.ui.theme.editor.activity

import androidx.activity.viewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ThemeEditorActivityAppBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity

class ThemeEditorActivity :
    BaseActivity<ThemeEditorViewModel, ThemeEditorActivityAppBinding>(R.layout.theme_editor_activity_app) {

    private val viewModel: ThemeEditorViewModel by viewModels()

    override fun setupUI() {

    }

    override fun provideViewModel() = viewModel
}
