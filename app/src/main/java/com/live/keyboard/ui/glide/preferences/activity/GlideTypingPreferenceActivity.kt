package com.live.keyboard.ui.glide.preferences.activity

import androidx.activity.viewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.GlidePreferencesActivityBinding
import com.live.keyboard.ui.base.BaseActivity

class GlideTypingPreferenceActivity :
    BaseActivity<GlideTypingPreferenceViewModel, GlidePreferencesActivityBinding>(R.layout.glide_preferences_activity) {

    private val viewModel: GlideTypingPreferenceViewModel by viewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }
}
