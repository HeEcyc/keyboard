package com.live.keyboard.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.live.keyboard.R
import com.live.keyboard.databinding.AnimationSwipeLayoutBinding

class SwipeAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val binding: AnimationSwipeLayoutBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.animation_swipe_layout,
        this,
        true
    )!!


    init {

    }
}
