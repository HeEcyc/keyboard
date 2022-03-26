package com.ioskey.iosboard.ui.guid.activity.second

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.GuidSettingsActivityBinding
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.ui.custom.SwipeAnimationView
import com.ioskey.iosboard.ui.guid.activity.third.GuidThemeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuidSettingsActivity : BaseActivity<GuidSettingsViewModel, GuidSettingsActivityBinding>(R.layout.guid_settings_activity) {

    val viewModel: GuidSettingsViewModel by viewModels()

    override fun setupUI() {
        binding.buttonExit.setOnClickListener { onBackPressed() }
        binding.buttonSet.setOnClickListener { onBackPressed() }
        lifecycleScope.launch {
            binding.animationView.initKeyboard()
            withContext(Dispatchers.Main) {
                binding.animationView.showKeyboardAnimation(
                    if (viewModel.isGlideTypingOn.get())
                        SwipeAnimationView.AnimationType.GETSURE
                    else
                        SwipeAnimationView.AnimationType.SWIPE
                )
                binding.buttonGlideTyping.setOnClickListener {
                    if (viewModel.onGlideTypingClick())
                        binding.animationView.showKeyboardAnimation(SwipeAnimationView.AnimationType.GETSURE)
                }
                binding.buttonKeyboardSwipe.setOnClickListener {
                    if (viewModel.onKeyboardSwipeClick())
                        binding.animationView.showKeyboardAnimation(SwipeAnimationView.AnimationType.SWIPE)
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, GuidThemeActivity::class.java))
        super.onBackPressed()
    }

    override fun provideViewModel() = viewModel
}
