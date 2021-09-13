package dev.patrickgold.florisboard.ui.preview.theme.activity

import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.data.KeyboardTheme
import dev.patrickgold.florisboard.databinding.ThemePreviewActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity
import dev.patrickgold.florisboard.ui.theme.editor.activity.ThemeEditorActivity
import dev.patrickgold.florisboard.util.BUNDLE_THEME_KEY


class ThemePreviewActivity :
    BaseActivity<ThemePreviewViewModel, ThemePreviewActivityBinding>(R.layout.theme_preview_activity),
    View.OnClickListener {

    private val currentTheme: KeyboardTheme by lazy { intent.getSerializableExtra(BUNDLE_THEME_KEY) as KeyboardTheme }

    private val viewModel: ThemePreviewViewModel by viewModels()

    override fun setupUI() {
        binding.backButton.setOnClickListener(this)
        binding.editButton.setOnClickListener(this)
    }

    override fun provideViewModel() = viewModel

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backButton -> onBackPressed()
            R.id.editButton -> editKeyboardTheme()
        }
        binding.exampleEditText.requestFocus()
        getSystemService(InputMethodManager::class.java)
            .showSoftInput(binding.exampleEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onPause() {
        super.onPause()
        val imm: InputMethodManager = getSystemService(InputMethodManager::class.java)
        if (imm.isActive) imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun editKeyboardTheme() {
        Intent(this, ThemeEditorActivity::class.java)
            .putExtra(BUNDLE_THEME_KEY, currentTheme)
            .let(::startActivity)
    }

}
