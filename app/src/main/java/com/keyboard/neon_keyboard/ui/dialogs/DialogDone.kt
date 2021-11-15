package com.keyboard.neon_keyboard.ui.dialogs

import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.databinding.DialogDoneBinding
import com.keyboard.neon_keyboard.ui.base.BaseDialog

class DialogDone : BaseDialog<DialogDoneBinding>(R.layout.dialog_done) {

    override fun setupUI() {
        binding.closeDialogButton.setOnClickListener { dismiss() }
    }

    override fun getTheme() = R.style.DialogTheme
}
