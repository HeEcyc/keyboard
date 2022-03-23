package com.ioskey.iosboard.ui.splash.activity

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.SplashActivityBinding
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.ui.main.activity.MainActivity

// todo open settings activity on special intent key

class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>(R.layout.splash_activity) {

    private val viewModel: SplashViewModel by viewModels()

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!Settings.canDrawOverlays(this)) {}
//                AlarmBroadcast.startAlarm(this)
            showMainActivity()
        }


    override fun provideViewModel() = viewModel

    override fun setupUI() {
//        askOverlayPermission()
        showMainActivity()
    }

    private fun askOverlayPermission() {
        if (!Settings.canDrawOverlays(this))
            overlayPermissionLauncher.launch(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    .setData(Uri.fromParts("package", packageName, null))
            )
        else
            showMainActivity()
    }

    private fun showMainActivity() {
        Intent(this, MainActivity::class.java)
            .let(::startActivity)
        finish()
    }
}
