package com.gg.osto.ui.splash

import android.annotation.SuppressLint
import androidx.activity.viewModels
import com.gg.osto.R
import com.gg.osto.databinding.SplashActivityBinding
import com.gg.osto.repository.PrefsReporitory
import com.gg.osto.ui.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>(R.layout.splash_activity) {

    private val viewModel: SplashViewModel by viewModels()

    override fun setupUI() {
        PrefsReporitory.isFirstLaunch = false
        binding.buttonStart.setOnClickListener { finish() }
    }

    override fun provideViewModel(): SplashViewModel = viewModel

}
