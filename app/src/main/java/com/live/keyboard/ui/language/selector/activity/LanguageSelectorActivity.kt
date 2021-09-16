package com.live.keyboard.ui.language.selector.activity

import androidx.activity.viewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.LanguageSelectorActivityBinding
import com.live.keyboard.ui.base.BaseActivity

class LanguageSelectorActivity :
    BaseActivity<LanguageSelectorViewModel, LanguageSelectorActivityBinding>(R.layout.language_selector_activity) {
    private val viewModel: LanguageSelectorViewModel by viewModels()

    var loadingDialog: LoadingDialog? = null

    override fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }
        viewModel.askDownloadLanguageEvents.observe(this) { language ->
            LanguageDownloadQuestionDialog(
                this,
                language,
                {
                    loadingDialog = LoadingDialog()
                    loadingDialog?.show(supportFragmentManager, null)
                    viewModel.downloadLanguage(language)
                }
            ).show()
        }
        viewModel.downloadingFinishedEvents.observe(this) {
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    override fun provideViewModel() = viewModel
}
