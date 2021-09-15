package com.live.keyboard.ui.dialogs

import com.live.keyboard.R
import com.live.keyboard.databinding.DialogDoneBinding
import com.live.keyboard.ui.base.BaseDialog

class DialogDone : BaseDialog<DialogDoneBinding>(R.layout.dialog_done) {

    override fun setupUI() {
        binding.closeDialogButton.setOnClickListener { dismiss() }
    }

    override fun getTheme() = R.style.DialogTheme
}
