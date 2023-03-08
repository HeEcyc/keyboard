package com.gg.osto.common

import android.content.Context
import android.util.AttributeSet
import android.widget.ViewFlipper

/**
 * Custom ViewFlipper class used to prevent an unnecessary exception to be thrown when it is
 * detached from a window.
 *
 * Based on the solution of this SO answer: https://stackoverflow.com/a/8208874/6801193
 */
class FlorisViewFlipper : ViewFlipper {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow()
        } catch (e: IllegalArgumentException) {
            stopFlipping()
        }
    }
}
