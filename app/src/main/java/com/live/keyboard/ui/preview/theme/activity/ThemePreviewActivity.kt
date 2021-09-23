package com.live.keyboard.ui.preview.theme.activity

import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.live.keyboard.R
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.databinding.ThemePreviewActivityBinding
import com.live.keyboard.ui.base.BaseActivity
import com.live.keyboard.ui.theme.editor.activity.ThemeEditorActivity
import com.live.keyboard.util.BUNDLE_THEME_KEY


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
        getSystemService(InputMethodManager::class.java)
            .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
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
