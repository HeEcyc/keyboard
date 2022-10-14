package com.smard.boart.ui.language

import androidx.activity.viewModels
import com.smard.boart.R
import com.smard.boart.databinding.LanguageActivityBinding
import com.smard.boart.ui.base.BaseActivity

class LanguageActivity :
    BaseActivity<LanguageViewModel, LanguageActivityBinding>(R.layout.language_activity) {
    private val viewModel: LanguageViewModel by viewModels()

    override fun setupUI() {
        binding.buttonBack.setOnClickListener { onBackPressed() }
    }

    override fun provideViewModel() = viewModel


}
