package dev.patrickgold.florisboard.ui.glide.preferences.activity

import androidx.activity.viewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.GlidePreferencesActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity

class GlideTypingPreferenceActivity :
    BaseActivity<GlideTypingPreferenceViewModel, GlidePreferencesActivityBinding>(R.layout.glide_preferences_activity) {

    private val viewModel: GlideTypingPreferenceViewModel by viewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }
}
