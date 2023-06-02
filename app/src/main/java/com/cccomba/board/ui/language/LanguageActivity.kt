package com.cccomba.board.ui.language

import androidx.activity.viewModels
import com.cccomba.board.R
import com.cccomba.board.databinding.LanguageActivityBinding
import com.cccomba.board.ui.base.BaseActivity

class LanguageActivity :
    BaseActivity<LanguageViewModel, LanguageActivityBinding>(R.layout.language_activity) {
    private val viewModel: LanguageViewModel by viewModels()

    override fun setupUI() {
        binding.buttonBack.setOnClickListener { onBackPressed() }
    }

    override fun provideViewModel() = viewModel


}
