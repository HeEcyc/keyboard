package com.live.keyboard.ui.custom

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.live.keyboard.R
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.databinding.AnimationSwipeLayoutBinding
import com.live.keyboard.ime.core.Subtype
import com.live.keyboard.ime.keyboard.ComputingEvaluator
import com.live.keyboard.ime.keyboard.DefaultComputingEvaluator
import com.live.keyboard.ime.keyboard.KeyData
import com.live.keyboard.ime.text.gestures.GlideTypingGesture
import com.live.keyboard.ime.text.key.CurrencySet
import com.live.keyboard.ime.text.key.KeyCode
import com.live.keyboard.ime.text.keyboard.KeyboardMode
import com.live.keyboard.ime.text.keyboard.TextKeyData
import com.live.keyboard.ime.text.keyboard.TextKeyboardIconSet
import com.live.keyboard.ime.text.layout.LayoutManager


class SwipeAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var animator: ObjectAnimator? = null
    var animationType: AnimationType = AnimationType.GETSURE
    private val word = "helo"
    private val animationHandler = Handler(Looper.getMainLooper())

    val binding: AnimationSwipeLayoutBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.animation_swipe_layout,
        this,
        true
    )!!

    fun prepareKeyboardAnimation() {
        when (animationType) {
            AnimationType.GETSURE -> prepareGetsureAnimation()
            AnimationType.SWIPE -> prepareSwipeAnimation()
        }
    }

    init {
        binding.previewEditText.requestFocus()
    }

    suspend fun initKeyboard() {

        binding.keyboardPreview.setIconSet(TextKeyboardIconSet.new(context))
        binding.keyboardPreview.setComputingEvaluator(getEvalutor())
        binding.keyboardPreview.sync()
        binding.keyboardPreview.setComputedKeyboard(
            LayoutManager().computeKeyboardAsync(
                KeyboardMode.CHARACTERS,
                Subtype.DEFAULT
            ).await(), KeyboardTheme()
        )
    }

    fun getEvalutor() = object : ComputingEvaluator by DefaultComputingEvaluator {
        override fun evaluateVisible(data: KeyData): Boolean {
            return data.code != KeyCode.SWITCH_TO_MEDIA_CONTEXT
        }

        override fun isSlot(data: KeyData): Boolean {
            return CurrencySet.isCurrencySlot(data.code)
        }

        override fun getSlotData(data: KeyData): KeyData {
            return TextKeyData(label = ".")
        }
    }

    private fun prepareSwipeAnimation() {
        binding.keyboardPreview.deactiveGlideTyping()
        binding.swipeHand.x = (binding.keyboardConstrain.width / 2).toFloat()
        binding.swipeHand.y = (binding.keyboardConstrain.height / 2).toFloat()
        binding.previewEditText.setText(R.string.example_text)
        binding.previewEditText.setSelection(binding.previewEditText.text?.length ?: 0)
    }

    private fun startKeyboardAnimation() {
        when (animationType) {
            AnimationType.GETSURE -> startGetsureAnimation()
            AnimationType.SWIPE -> startSwipeAnimation(0.2f)
        }
    }

    private fun startGetsureAnimation() {
        moveToKey(1)
    }

    private fun moveToKey(keyIndex: Int) {
        if (keyIndex == word.length) {
            binding.previewEditText.setText(R.string.example_text)
            binding.previewEditText.setSelection(binding.previewEditText.text?.length ?: 0)
            animationHandler.postDelayed({
                animateKeyboard()
            }, 2000)
        } else animateToKey(keyIndex)
    }

    private fun animateKeyboard() {
        clearKeyboardAnimation()
        prepareKeyboardAnimation()
        startKeyboardAnimation()
    }

    private fun animateToKey(keyIndex: Int) {
        binding.keyboardPreview.findKeyView(word[keyIndex].toString())?.let {
            val pvhX = PropertyValuesHolder.ofFloat(X, it.left.toFloat())
            val pvhY = PropertyValuesHolder.ofFloat(Y, (it.bottom + binding.toolbar.height / 2).toFloat())
            animator = ObjectAnimator.ofPropertyValuesHolder(binding.swipeHand, pvhX, pvhY).apply {
                doOnEnd { moveToKey(keyIndex + 1) }
                addUpdateListener {
                    val glideXPosition = binding.swipeHand.x + (binding.swipeHand.width / 2.5).toInt()
                    val glideYPosition = binding.swipeHand.y - binding.swipeHand.height / 2
                    binding.keyboardPreview
                        .onGlideAddPoint(GlideTypingGesture.Detector.Position(glideXPosition, glideYPosition))
                }
                duration = 1000
                start()
            }
        }
    }

    private fun startSwipeAnimation(multiply: Float) {
        val position = binding.keyboardPreview.width * multiply
        animator = ObjectAnimator.ofFloat(binding.swipeHand, "x", position).apply {
            doOnEnd { setMiddlePosition(multiply) }
            duration = 1000
            interpolator = FastOutSlowInInterpolator()
            start()
        }
    }

    private fun setMiddlePosition(multiply: Float) {

        if (multiply < 0.5) binding.previewEditText.setText("")
        else binding.previewEditText.append(" ")

        animationHandler.postDelayed({
            prepareSwipeAnimation()
            startSwipeAnimation(1 - multiply)
        }, 2000)

    }

    private fun prepareGetsureAnimation() {
        binding.keyboardPreview.activeGliding()
        binding.previewEditText.setText("")
        binding.keyboardPreview.findKeyView(word[0].toString())?.let {
            binding.swipeHand.x = it.left.toFloat()
            binding.swipeHand.y = (it.bottom + binding.toolbar.height / 2).toFloat()
        }
    }

    enum class AnimationType {
        GETSURE,
        SWIPE
    }

    fun showKeyboardAnimation(animationType: AnimationType) {
        this.animationType = animationType
        clearKeyboardAnimation()
        animateKeyboard()
    }

    private fun clearKeyboardAnimation() {
        animator?.pause()
        animationHandler.removeCallbacksAndMessages(null)
        binding.swipeHand.clearAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearKeyboardAnimation()
    }
}

