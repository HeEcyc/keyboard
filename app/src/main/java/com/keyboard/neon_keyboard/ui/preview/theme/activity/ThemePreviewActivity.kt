package com.keyboard.neon_keyboard.ui.preview.theme.activity

import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.data.KeyboardTheme
import com.keyboard.neon_keyboard.databinding.ThemePreviewActivityBinding
import com.keyboard.neon_keyboard.ui.base.BaseActivity
import com.keyboard.neon_keyboard.ui.theme.editor.activity.ThemeEditorActivity
import com.keyboard.neon_keyboard.util.BUNDLE_THEME_KEY


class ThemePreviewActivity :
    BaseActivity<ThemePreviewViewModel, ThemePreviewActivityBinding>(R.layout.theme_preview_activity),
    View.OnClickListener {

    private val currentTheme: KeyboardTheme by lazy { intent.getSerializableExtra(BUNDLE_THEME_KEY) as KeyboardTheme }

    private val viewModel: ThemePreviewViewModel by viewModels()

    override fun setupUI() {
        binding.backButton.setOnClickListener(this)
        binding.editButton.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        binding.exampleEditText.requestFocus()
    }

    override fun provideViewModel() = viewModel

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backButton -> onBackPressed()
            R.id.editButton -> editKeyboardTheme()
        }
    }

    private fun editKeyboardTheme() {
        getSystemService(InputMethodManager::class.java)
            .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        Intent(this, ThemeEditorActivity::class.java)
            .putExtra(BUNDLE_THEME_KEY, currentTheme)
            .let(::startActivity)
    }

}
