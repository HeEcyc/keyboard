package com.keyboard.neon_keyboard.ui.language.selector.activity

import androidx.activity.viewModels
import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.databinding.LanguageSelectorActivityBinding
import com.keyboard.neon_keyboard.ui.base.BaseActivity

class LanguageSelectorActivity :
    BaseActivity<LanguageSelectorViewModel, LanguageSelectorActivityBinding>(R.layout.language_selector_activity) {
    private val viewModel: LanguageSelectorViewModel by viewModels()

    override fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    override fun provideViewModel() = viewModel
}