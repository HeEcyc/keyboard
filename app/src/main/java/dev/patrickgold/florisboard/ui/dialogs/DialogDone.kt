package dev.patrickgold.florisboard.ui.dialogs

import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.DialogDoneBinding
import dev.patrickgold.florisboard.ui.base.BaseDialog

class DialogDone : BaseDialog<DialogDoneBinding>(R.layout.dialog_done) {

    override fun setupUI() {
        binding.closeDialogButton.setOnClickListener { dismiss() }
    }

    override fun getTheme() = R.style.DialogTheme
}
