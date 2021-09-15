package com.live.keyboard.ui.language.selector.activity

import androidx.activity.viewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.LanguageSelectorActivityBinding
import com.live.keyboard.ui.base.BaseActivity

class LanguageSelectorActivity :
    BaseActivity<LanguageSelectorViewModel, LanguageSelectorActivityBinding>(R.layout.language_selector_activity) {
    private val viewModel: LanguageSelectorViewModel by viewModels()

    override fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    override fun provideViewModel() = viewModel
}
