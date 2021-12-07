package com.neonkeyboard.cool.ui.language.selector.activity

import androidx.activity.viewModels
import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.databinding.LanguageSelectorActivityBinding
import com.neonkeyboard.cool.ui.base.BaseActivity

class LanguageSelectorActivity :
    BaseActivity<LanguageSelectorViewModel, LanguageSelectorActivityBinding>(R.layout.language_selector_activity) {
    private val viewModel: LanguageSelectorViewModel by viewModels()

    override fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    override fun provideViewModel() = viewModel
}
