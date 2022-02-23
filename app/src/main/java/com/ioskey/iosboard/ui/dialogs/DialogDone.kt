package com.ioskey.iosboard.ui.dialogs

import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.DialogDoneBinding
import com.ioskey.iosboard.ui.base.BaseDialog

class DialogDone : BaseDialog<DialogDoneBinding>(R.layout.dialog_done) {

    override fun setupUI() {
        binding.closeDialogButton.setOnClickListener { dismiss() }
    }

    override fun getTheme() = R.style.DialogTheme
}
