package com.live.keyboard.ui.dialogs

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.live.keyboard.R
import com.live.keyboard.databinding.DialogGetsureSettigsBinding
import com.live.keyboard.ui.base.BaseDialog
import com.live.keyboard.ui.custom.SwipeAnimationView
import kotlinx.coroutines.launch

class DialogGetsureSettings : BaseDialog<DialogGetsureSettigsBinding>(R.layout.dialog_getsure_settigs) {

    override fun setupUI() {

        lifecycleScope.launch { binding.swipeAnitmationView.initKeyboard() }

        binding.getsureChooser.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == i) when (i) {
                R.id.getsure -> SwipeAnimationView.AnimationType.GETSURE
                else -> SwipeAnimationView.AnimationType.SWIPE
            }.let(binding.swipeAnitmationView::showKeyboardAnimation)
        }

        Handler(Looper.getMainLooper())
            .postDelayed(
                { binding.swipeAnitmationView.showKeyboardAnimation(SwipeAnimationView.AnimationType.GETSURE) },
                1000
            )
    }

    override fun getTheme() = R.style.DialogTheme
}
