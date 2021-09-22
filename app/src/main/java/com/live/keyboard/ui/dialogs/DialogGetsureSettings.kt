package com.live.keyboard.ui.dialogs

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.live.keyboard.R
import com.live.keyboard.databinding.DialogGetsureSettigsBinding
import com.live.keyboard.repository.PrefsReporitory
import com.live.keyboard.ui.base.BaseDialog
import com.live.keyboard.ui.custom.SwipeAnimationView
import kotlinx.coroutines.launch

class DialogGetsureSettings : BaseDialog<DialogGetsureSettigsBinding>(R.layout.dialog_getsure_settigs) {

    private var selected = SwipeAnimationView.AnimationType.GETSURE

    var onClosed: () -> Unit = {}

    override fun setupUI() {
        isCancelable = false

        lifecycleScope.launch { binding.swipeAnitmationView.initKeyboard() }

        binding.getsureChooser.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == i) when (i) {
                R.id.getsure -> SwipeAnimationView.AnimationType.GETSURE
                else -> SwipeAnimationView.AnimationType.SWIPE
            }.also { selected = it }.let(binding.swipeAnitmationView::showKeyboardAnimation)
        }

        binding.save.setOnClickListener {
            when (selected) {
                SwipeAnimationView.AnimationType.SWIPE -> PrefsReporitory.Settings.keyboardSwipe = true
                SwipeAnimationView.AnimationType.GETSURE -> PrefsReporitory.Settings.GlideTyping.enableGlideTyping = true
            }
            onClosed()
            dismiss()
        }

        binding.closeDialogButton.setOnClickListener { dismiss(); onClosed() }

        Handler(Looper.getMainLooper())
            .postDelayed(
                { binding.swipeAnitmationView.showKeyboardAnimation(SwipeAnimationView.AnimationType.GETSURE) },
                1000
            )
    }

    override fun getTheme() = R.style.DialogTheme
}
