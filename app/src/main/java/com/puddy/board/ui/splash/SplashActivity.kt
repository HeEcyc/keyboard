package com.puddy.board.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.puddy.board.R
import com.puddy.board.databinding.SplashActivityBinding
import com.puddy.board.ui.base.BaseActivity

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
        binding.img.setImageResource(permission.imgRes)
        binding.text.setText(permission.textRes)
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

    private sealed class Permission(val imgRes: Int, val textRes: Int) {
        abstract fun askPermission(sa: SplashActivity)
        abstract fun hasPermission(sa: SplashActivity): Boolean
    }

    private object OVERLAY : Permission(R.mipmap.img_splash_1, R.string.give_permissions_for_the_napplication_work_properly) {
        override fun askPermission(sa: SplashActivity) = sa.askOverlayPermission()
        override fun hasPermission(sa: SplashActivity) = Settings.canDrawOverlays(sa)
    }

    private object KEYBOARD : Permission(R.mipmap.img_splash_2, R.string.set_appclication_as_your_default_nphone_app_this_is_necessary_for_ncorrect_work_application) {
        override fun askPermission(sa: SplashActivity) = sa.askKeyboardPermission()
        override fun hasPermission(sa: SplashActivity) = sa.hasKeyboardPermission()
    }

}
