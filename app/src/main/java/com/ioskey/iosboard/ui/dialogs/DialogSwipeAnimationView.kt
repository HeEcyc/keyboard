package com.ioskey.iosboard.ui.dialogs

import android.view.Gravity
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.DialogSwipeAnimationViewBinding
import com.ioskey.iosboard.ui.base.BaseDialog
import com.ioskey.iosboard.ui.custom.SwipeAnimationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogSwipeAnimationView : BaseDialog<DialogSwipeAnimationViewBinding>(R.layout.dialog_swipe_animation_view) {

    var animationType = SwipeAnimationView.AnimationType.GETSURE

    override fun setupUI() {
        binding.title.setText(animationType.descriptionRes)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.animationView.initKeyboard()
            withContext(Dispatchers.Main) {
                binding.animationView.showKeyboardAnimation(animationType)
                binding.closeButton.setOnClickListener { dismiss() }
            }
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
