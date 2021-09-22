package com.live.keyboard.ui.dialogs

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.live.keyboard.R
import com.live.keyboard.databinding.DialogGetsureSettigsBinding
import com.live.keyboard.ui.base.BaseDialog
import kotlinx.coroutines.launch

class DialogGetsureSettings : BaseDialog<DialogGetsureSettigsBinding>(R.layout.dialog_getsure_settigs) {

    override fun setupUI() {
        lifecycleScope.launch {
            binding.swipeAnitmationView.initKeyboard()
        }
        Handler(Looper.getMainLooper())
            .postDelayed({ binding.swipeAnitmationView.animateKeyboard() }, 2000)
    }

    override fun getTheme() = R.style.DialogTheme
}
