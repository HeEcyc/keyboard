package com.gg.osto.ui.language

import androidx.activity.viewModels
import com.gg.osto.R
import com.gg.osto.databinding.LanguageActivityBinding
import com.gg.osto.ui.base.BaseActivity

class LanguageActivity :
    BaseActivity<LanguageViewModel, LanguageActivityBinding>(R.layout.language_activity) {
    private val viewModel: LanguageViewModel by viewModels()

    override fun setupUI() {
        binding.buttonBack.setOnClickListener { onBackPressed() }
    }

    override fun provideViewModel() = viewModel


}
