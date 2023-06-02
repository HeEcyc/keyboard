package com.cccomba.board.ui.splash

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import com.cccomba.board.R
import com.cccomba.board.databinding.SplashActivityBinding
import com.cccomba.board.repository.PrefsReporitory
import com.cccomba.board.ui.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>(R.layout.splash_activity) {

    private val viewModel: SplashViewModel by viewModels()

    override fun setupUI() {
        PrefsReporitory.isFirstLaunch = false
        binding.root.postDelayed(2000) { finish() }
    }

    override fun provideViewModel(): SplashViewModel = viewModel

}
