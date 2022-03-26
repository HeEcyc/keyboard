package com.ioskey.iosboard.ui.language.selector.activity

import androidx.activity.viewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.LanguageSelectorActivityBinding
import com.ioskey.iosboard.ui.base.BaseActivity

class LanguageSelectorActivity :
    BaseActivity<LanguageSelectorViewModel, LanguageSelectorActivityBinding>(R.layout.language_selector_activity) {
    private val viewModel: LanguageSelectorViewModel by viewModels()

    override fun setupUI() {
        binding.mode = Mode.DEFAULT
        binding.backButton.setOnClickListener { onBackPressed() }
        binding.buttonSet.setOnClickListener { onBackPressed() }
    }

    override fun provideViewModel() = viewModel

    enum class Mode {
        DEFAULT, GUID
    }

}
