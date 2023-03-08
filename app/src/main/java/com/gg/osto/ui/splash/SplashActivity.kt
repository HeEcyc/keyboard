package com.gg.osto.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.gg.osto.R
import com.gg.osto.databinding.SplashActivityBinding
import com.gg.osto.ui.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<SplashViewModel, SplashActivityBinding>(R.layout.splash_activity) {

    private val viewModel: SplashViewModel by viewModels()

    private val inputManager by lazy { getSystemService(InputMethodManager::class.java) }

    private var permission: Permission = OVERLAY

    override fun setupUI() {
        binding.buttonCancel.setOnClickListener { onBackPressed() }
        binding.buttonOk.setOnClickListener {
            if (!permission.hasPermission(this))
                permission.askPermission(this)
            else
                onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        permission = if (!Settings.canDrawOverlays(this)) OVERLAY else KEYBOARD
    }

    fun hasKeyboardPermission() = isKeyboardActive() && isKeyboardEnable()

    private fun isKeyboardActive() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == packageName } != null

    private fun isKeyboardEnable() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == packageName }
        ?.id == Settings.Secure.getString(contentResolver, "default_input_method")

    override fun provideViewModel(): SplashViewModel = viewModel

    fun askOverlayPermission() {
        startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                .setData(Uri.fromParts("package", packageName, null))
        )
    }

    fun askKeyboardPermission() {
        if (!isKeyboardActive())
            startActivity(Intent("android.settings.INPUT_METHOD_SETTINGS"))
        else
            inputManager.showInputMethodPicker()
    }

    private sealed class Permission {
        abstract fun askPermission(sa: SplashActivity)
        abstract fun hasPermission(sa: SplashActivity): Boolean
    }

    private object OVERLAY : Permission() {
        override fun askPermission(sa: SplashActivity) = sa.askOverlayPermission()
        override fun hasPermission(sa: SplashActivity) = Settings.canDrawOverlays(sa)
    }

    private object KEYBOARD : Permission() {
        override fun askPermission(sa: SplashActivity) = sa.askKeyboardPermission()
        override fun hasPermission(sa: SplashActivity) = sa.hasKeyboardPermission()
    }

}
