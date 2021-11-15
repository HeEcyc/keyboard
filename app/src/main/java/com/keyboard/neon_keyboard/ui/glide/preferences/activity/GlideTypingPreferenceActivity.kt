package com.keyboard.neon_keyboard.ui.glide.preferences.activity

import androidx.activity.viewModels
import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.databinding.GlidePreferencesActivityBinding
import com.keyboard.neon_keyboard.ui.base.BaseActivity

class GlideTypingPreferenceActivity :
    BaseActivity<GlideTypingPreferenceViewModel, GlidePreferencesActivityBinding>(R.layout.glide_preferences_activity) {

    private val viewModel: GlideTypingPreferenceViewModel by viewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }
}
