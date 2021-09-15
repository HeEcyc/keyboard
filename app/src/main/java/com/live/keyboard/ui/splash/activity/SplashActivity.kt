package com.live.keyboard.ui.splash.activity

import android.content.Intent
import androidx.activity.viewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.SplashActivityBinding
import com.live.keyboard.ui.base.BaseActivity
import com.live.keyboard.ui.main.activity.MainActivity

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
