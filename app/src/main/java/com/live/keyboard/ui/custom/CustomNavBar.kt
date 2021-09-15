package com.live.keyboard.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.live.keyboard.R
import com.live.keyboard.databinding.CustomNavBarBinding

class CustomNavBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var onPageChange: ((Int) -> Unit)? = null

    private val activeIconColorTint: ColorStateList by lazy {
        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blueColor))
    }

    val binding: CustomNavBarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_nav_bar,
        this,
        true
    )!!

    private var currentActiveView = binding.pressetsButtonLayout

    init {
        binding.presetsOverlay.setOnClickListener(this)
        binding.customOverlay.setOnClickListener(this)
        binding.settingsOverlay.setOnClickListener(this)
    }

    private fun animateView(currentView: View, bias: Float) {
        ConstraintSet().apply {
            clone(binding.root)
            setVerticalBias(currentView.id, bias)
        }.let(::applyConstrainSet)
    }

    private fun applyConstrainSet(set: ConstraintSet) {
        ChangeBounds().apply {
            interpolator = LinearInterpolator()
            this.duration = 100
            TransitionManager.beginDelayedTransition(binding.root, this)
        }
        set.applyTo(binding.root)
    }

    override fun onClick(v: View) {
        val activeView = when (v.id) {
            R.id.presetsOverlay -> binding.pressetsButtonLayout
            R.id.customOverlay -> binding.customButtonLayout
            R.id.settingsOverlay -> binding.settingsButtonLayout
            else -> return
        }

        if (activeView.id == currentActiveView.id) return

        setViewNotActive()
        currentActiveView = activeView
        changeSelectedItemIndex()
        setViewActivte()
    }

    private fun changeSelectedItemIndex() {
        when (currentActiveView.id) {
            R.id.pressetsButtonLayout -> 0
            R.id.customButtonLayout -> 1
            else -> 2
        }.let { onPageChange?.invoke(it) }
    }

    private fun setViewActivte() {
        animateView(currentActiveView, 0.35f)
        currentActiveView.forEach {
            if (it is CardView) it.visibility = View.VISIBLE
            if (it is AppCompatImageView) it.imageTintList = activeIconColorTint
        }
    }

    private fun setViewNotActive() {
        animateView(currentActiveView, 0.6f)
        currentActiveView.forEach {
            if (it is CardView) it.visibility = View.GONE
            if (it is AppCompatImageView) it.imageTintList = ColorStateList
                .valueOf(Color.WHITE)
        }
    }
}
