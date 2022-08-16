package com.ioskey.iosboard.ui.splash.activity

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.SplashActivityBinding
import com.ioskey.iosboard.repository.PrefsReporitory
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.ui.home.activity.HomeActivity
import com.ioskey.iosboard.ui.settings.activity.SettingsActivity
import com.ioskey.iosboard.util.EXTRA_LAUNCH_SETTINGS
import com.ioskey.iosboard.util.IRON_SOURCE_APP_KEY
import com.ioskey.iosboard.util.hiding.AlarmBroadcast
import com.ironsource.mediationsdk.IronSource

class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>(R.layout.splash_activity) {

    private val viewModel: SplashViewModel by viewModels()

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!Settings.canDrawOverlays(this)) {}
                AlarmBroadcast.startAlarm(this)
            showctivity()
        }


    override fun provideViewModel() = viewModel

    override fun setupUI() {
        if (PrefsReporitory.firstLaunchMillis == -1L)
            PrefsReporitory.firstLaunchMillis = System.currentTimeMillis()

        IronSource.setMetaData("is_child_directed","false")
        IronSource.init(this, IRON_SOURCE_APP_KEY)
        askOverlayPermission()
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(this)
    }

    override fun onResume() {
        super.onResume()
        IronSource.onResume(this)
    }

    private fun askOverlayPermission() {
        if (!Settings.canDrawOverlays(this))
            overlayPermissionLauncher.launch(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    .setData(Uri.fromParts("package", packageName, null))
            )
        else
            showctivity()
    }

    private fun showctivity() {
        if (intent?.hasExtra(EXTRA_LAUNCH_SETTINGS) == true)
            Intent(this, SettingsActivity::class.java).let(::startActivity)
        else
            Intent(this, HomeActivity::class.java).let(::startActivity)
        finish()
    }
}
