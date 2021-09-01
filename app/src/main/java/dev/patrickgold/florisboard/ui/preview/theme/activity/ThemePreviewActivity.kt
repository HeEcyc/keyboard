package dev.patrickgold.florisboard.ui.preview.theme.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ThemePreviewActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity
import dev.patrickgold.florisboard.ui.theme.editor.activity.ThemeEditorActivity

class ThemePreviewActivity :
    BaseActivity<ThemePreviewViewModel, ThemePreviewActivityBinding>(R.layout.theme_preview_activity),
    View.OnClickListener {

    private val viewModel: ThemePreviewViewModel by viewModels()

    override fun setupUI() {
        binding.backButton.setOnClickListener(this)
        binding.saveButton.setOnClickListener(this)
        binding.editButton.setOnClickListener(this)
    }

    override fun provideViewModel() = viewModel

    override fun onClick(v: View) {
        when (v.id) {
            R.id.saveButton -> saveKeyboardTheme()
            R.id.editButton -> editKeyboardTheme()
        }
        finish()
    }

    private fun editKeyboardTheme() {
        startActivity(Intent(this, ThemeEditorActivity::class.java))
    }

    private fun saveKeyboardTheme() {

    }

}
