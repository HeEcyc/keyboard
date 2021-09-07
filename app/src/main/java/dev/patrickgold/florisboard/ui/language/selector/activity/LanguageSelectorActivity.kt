package dev.patrickgold.florisboard.ui.language.selector.activity

import androidx.activity.viewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.LanguageSelectorActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity

class LanguageSelectorActivity :
    BaseActivity<LanguageSelectorViewModel, LanguageSelectorActivityBinding>(R.layout.language_selector_activity) {
    private val viewModel: LanguageSelectorViewModel by viewModels()

    override fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    override fun provideViewModel() = viewModel
}
