package com.neonkeyboard.cool.ui.splash.activity

import android.content.Intent
import androidx.activity.viewModels
import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.databinding.SplashActivityBinding
import com.neonkeyboard.cool.ui.base.BaseActivity
import com.neonkeyboard.cool.ui.main.activity.MainActivity

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
