package dev.patrickgold.florisboard.ui.dialogs

import android.os.Handler
import android.os.Looper
import android.view.ViewTreeObserver
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.DialogPermissionsBinding
import dev.patrickgold.florisboard.ui.base.BaseDialog

class DialogPermissions(private val onPermissionAction: OnPermissionAction) :
    BaseDialog<DialogPermissionsBinding>(R.layout.dialog_permissions) {

    private val focusListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        if (hasFocus && onPermissionAction.hasAllPermissions()) {
            removeListener()
            Handler(Looper.getMainLooper()).postDelayed({ dismiss() }, 200)
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
    }
}
