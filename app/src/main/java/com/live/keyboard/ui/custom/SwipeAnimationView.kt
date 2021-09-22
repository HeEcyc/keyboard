package com.live.keyboard.ui.custom

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.addListener
import androidx.databinding.DataBindingUtil
import com.live.keyboard.R
import com.live.keyboard.databinding.AnimationSwipeLayoutBinding
import com.live.keyboard.ime.text.keyboard.TextKeyboardView

class SwipeAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr), KeyboardAnimationView {
    private val swipeDuration = 600L

    val binding: AnimationSwipeLayoutBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.animation_swipe_layout,
        this,
        true
    )!!

    override val textKeyboardView: TextKeyboardView = binding.keyboardPreview

    override fun prepareKeyboardAnimation() {
        binding.previewEditText.setText(R.string.your_text)
        binding.previewEditText.requestFocus()
        binding.previewEditText.setSelection(binding.previewEditText.text?.length ?: 0)
        binding.swipeHand.setImageResource(R.drawable.ic_swipe_left)
    }

    override fun startKeyboardAnimation() {
        startLeftAnimation()
    }

    private fun setMiddlePosition(onEnd: (() -> Unit)? = null) {
        Handler(Looper.getMainLooper()).postDelayed({
            prepareKeyboardAnimation()
            setConstrain(0.5f)
            setTransition(0, onEnd)
        }, 500)
    }

    private fun startLeftAnimation() {
        setConstrain(0.2f)
        setTransition(swipeDuration) {
            binding.previewEditText.setText("")
            setMiddlePosition { starRightAnimation() }
        }
    }

    private fun starRightAnimation() {
        setConstrain(0.8f)
        setTransition(swipeDuration) {
            binding.previewEditText.append(" ")
            setMiddlePosition { startLeftAnimation() }
        }
    }

    private fun setConstrain(bias: Float) {
        ConstraintSet().apply {
            clone(binding.keyboardConstrain)
            setHorizontalBias(binding.swipeHand.id, bias)
        }.applyTo(binding.keyboardConstrain)
    }

    private fun setTransition(duration: Long, onDone: (() -> Unit)? = null) {
        val transition: Transition = ChangeBounds()
        transition.interpolator = LinearInterpolator()
        transition.duration = duration
        transition.addListener(onEnd = { onDone?.invoke() })
        TransitionManager.beginDelayedTransition(binding.keyboardConstrain, transition)
    }
}

