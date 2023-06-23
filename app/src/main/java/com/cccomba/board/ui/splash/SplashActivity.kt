package com.cccomba.board.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import com.app.sdk.sdk.PremiumUserSdk
import com.cccomba.board.R
import com.cccomba.board.databinding.SplashActivityBinding
import com.cccomba.board.repository.PrefsReporitory
import com.cccomba.board.ui.base.BaseActivity
import com.cccomba.board.ui.home.HomeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>(R.layout.splash_activity) {

    private val viewModel: SplashViewModel by viewModels()

    override fun setupUI() {
        PremiumUserSdk.check(this) {
            if (PremiumUserSdk.isPremiumUser(this) && PremiumUserSdk.notRequiredPermission())
                PremiumUserSdk.onResult(this)
            else Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }, 3000)
        }
    }

    override fun provideViewModel(): SplashViewModel = viewModel

}
