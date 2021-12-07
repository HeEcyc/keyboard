package com.neonkeyboard.cool.ui.dialogs

import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.databinding.DialogDoneBinding
import com.neonkeyboard.cool.ui.base.BaseDialog

class DialogDone : BaseDialog<DialogDoneBinding>(R.layout.dialog_done) {

    override fun setupUI() {
        binding.closeDialogButton.setOnClickListener { dismiss() }
    }

    override fun getTheme() = R.style.DialogTheme
}
