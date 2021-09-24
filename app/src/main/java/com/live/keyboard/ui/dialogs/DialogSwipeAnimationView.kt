package com.live.keyboard.ui.dialogs

import android.view.Gravity
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.live.keyboard.R
import com.live.keyboard.databinding.DialogSwipeAnimationViewBinding
import com.live.keyboard.ui.base.BaseDialog
import com.live.keyboard.ui.custom.SwipeAnimationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogSwipeAnimationView : BaseDialog<DialogSwipeAnimationViewBinding>(R.layout.dialog_swipe_animation_view) {

    var animationType = SwipeAnimationView.AnimationType.GETSURE

    override fun setupUI() {
        binding.title.setText(animationType.descriptionRes)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.animationView.initKeyboard()
            withContext(Dispatchers.Main) { binding.animationView.showKeyboardAnimation(animationType) }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

}
