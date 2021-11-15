package com.keyboard.neon_keyboard.ui.splash.activity

import android.content.Intent
import androidx.activity.viewModels
import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.databinding.SplashActivityBinding
import com.keyboard.neon_keyboard.ui.base.BaseActivity
import com.keyboard.neon_keyboard.ui.main.activity.MainActivity

class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>(R.layout.splash_activity) {

    private val viewModel: SplashViewModel by viewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        showMainActivity()
    }

    private fun showMainActivity() {
        Intent(this, MainActivity::class.java)
            .let(::startActivity)
        finish()
    }
}
