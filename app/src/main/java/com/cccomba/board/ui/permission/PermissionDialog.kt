package com.cccomba.board.ui.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.cccomba.board.R
import com.cccomba.board.databinding.PermissionDialogBinding
import com.cccomba.board.ui.base.BaseDialog

class PermissionDialog : BaseDialog<PermissionDialogBinding>(R.layout.permission_dialog) {

    private val inputManager by lazy { getSystemService(requireContext(), InputMethodManager::class.java) as InputMethodManager }

    private var permission: Permission = OVERLAY

    override fun setupUI() {
        permission.showProperLayout(this)
        binding.buttonClose.setOnClickListener { dismiss() }
        binding.buttonCancel.setOnClickListener { dismiss() }
        binding.buttonOk.setOnClickListener {
            if (!permission.hasPermission(this))
                permission.askPermission(this)
            else
                dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        permission = if (!Settings.canDrawOverlays(requireContext())) OVERLAY else KEYBOARD
        permission.showProperLayout(this)
    }

    fun hasKeyboardPermission() = isKeyboardActive() && isKeyboardEnable()

    private fun isKeyboardActive() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == requireContext().packageName } != null

    private fun isKeyboardEnable() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == requireContext().packageName }
        ?.id == Settings.Secure.getString(requireContext().contentResolver, "default_input_method")

    fun askOverlayPermission() {
        startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                .setData(Uri.fromParts("package", requireContext().packageName, null))
        )
    }

    fun askKeyboardPermission() {
        if (!isKeyboardActive())
            startActivity(Intent("android.settings.INPUT_METHOD_SETTINGS"))
        else
            inputManager.showInputMethodPicker()
    }

    private sealed class Permission {
        abstract fun askPermission(pd: PermissionDialog)
        abstract fun hasPermission(pd: PermissionDialog): Boolean
        abstract fun showProperLayout(pd: PermissionDialog)
    }

    private object OVERLAY : Permission() {
        override fun askPermission(pd: PermissionDialog) = pd.askOverlayPermission()
        override fun hasPermission(pd: PermissionDialog) = Settings.canDrawOverlays(pd.requireContext())
        override fun showProperLayout(pd: PermissionDialog) {
            pd.binding.layoutKeyboardPermission.visibility = View.GONE
            pd.binding.layoutOverlayPermission.visibility = View.VISIBLE
        }
    }

    private object KEYBOARD : Permission() {
        override fun askPermission(pd: PermissionDialog) = pd.askKeyboardPermission()
        override fun hasPermission(pd: PermissionDialog) = pd.hasKeyboardPermission()
        override fun showProperLayout(pd: PermissionDialog) {
            pd.binding.layoutOverlayPermission.visibility = View.GONE
            pd.binding.layoutKeyboardPermission.visibility = View.VISIBLE
        }
    }

}
