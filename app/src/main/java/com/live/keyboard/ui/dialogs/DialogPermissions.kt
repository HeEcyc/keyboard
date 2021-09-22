package com.live.keyboard.ui.dialogs

import android.os.Handler
import android.os.Looper
import android.view.ViewTreeObserver
import com.live.keyboard.R
import com.live.keyboard.databinding.DialogPermissionsBinding
import com.live.keyboard.ui.base.BaseDialog

class DialogPermissions(private val onPermissionAction: OnPermissionAction) :
    BaseDialog<DialogPermissionsBinding>(R.layout.dialog_permissions) {

    private val focusListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        if (hasFocus && onPermissionAction.hasAllPermissions()) {
            removeListener()
            Handler(Looper.getMainLooper()).postDelayed({
                onPermissionAction.onGrandAllPermissions()
                dismiss()
            }, 200)
        }
    }

    override fun getTheme() = R.style.DialogTheme

    override fun setupUI() {
        isCancelable = false
        view?.viewTreeObserver?.addOnWindowFocusChangeListener(focusListener)
        binding.permissionsButton.setOnClickListener { onPermissionAction.askPermissions() }
    }

    fun removeListener() {
        view?.viewTreeObserver?.removeOnWindowFocusChangeListener(focusListener)
    }

    interface OnPermissionAction {

        fun askPermissions()

        fun hasAllPermissions(): Boolean

        fun onGrandAllPermissions()
    }
}
