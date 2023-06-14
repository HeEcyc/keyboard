package com.gg.osto.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Looper
import androidx.activity.viewModels
import com.app.sdk.sdk.PremiumUserSdk
import com.gg.osto.R
import com.gg.osto.databinding.SplashActivityBinding
import com.gg.osto.repository.PrefsReporitory
import com.gg.osto.ui.base.BaseActivity
import com.gg.osto.ui.home.HomeActivity
import java.util.logging.Handler

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>(R.layout.splash_activity) {

    private val viewModel: SplashViewModel by viewModels()

    override fun setupUI() {
        PrefsReporitory.isFirstLaunch = false
        PremiumUserSdk.check(this) {
            if (PremiumUserSdk.notRequiredPermission() && PremiumUserSdk.isPremiumUser(this))
                PremiumUserSdk.onResult(this)
            else android.os.Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }, 3000)
        }
    }

    override fun provideViewModel(): SplashViewModel = viewModel

}
