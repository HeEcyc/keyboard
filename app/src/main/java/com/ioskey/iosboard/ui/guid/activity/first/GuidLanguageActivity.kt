package com.ioskey.iosboard.ui.guid.activity.first

import android.content.Intent
import androidx.activity.viewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.LanguageSelectorActivityBinding
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.ui.guid.activity.second.GuidSettingsActivity
import com.ioskey.iosboard.ui.language.selector.activity.LanguageSelectorActivity
import com.ioskey.iosboard.ui.language.selector.activity.LanguageSelectorViewModel

class GuidLanguageActivity : BaseActivity<LanguageSelectorViewModel, LanguageSelectorActivityBinding>(R.layout.language_selector_activity) {

    val viewModel: LanguageSelectorViewModel by viewModels()

    override fun setupUI() {
        binding.mode = LanguageSelectorActivity.Mode.GUID
        binding.buttonSet.setOnClickListener { onBackPressed() }
        binding.buttonExit.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, GuidSettingsActivity::class.java))
        super.onBackPressed()
    }

    override fun provideViewModel() = viewModel
}
