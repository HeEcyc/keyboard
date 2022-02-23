package com.neonkeyboard.cool.ui.splash.activity

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.databinding.SplashActivityBinding
import com.neonkeyboard.cool.ui.base.BaseActivity
import com.neonkeyboard.cool.ui.main.activity.MainActivity

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
        askOverlayPermission()
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
