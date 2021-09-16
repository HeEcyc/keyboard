package com.live.keyboard.ime.core

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Root window view of the keyboard.
 */
class InputWindowView : FrameLayout {
    private val florisboard get() = FlorisBoard.getInstanceOrNull()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        florisboard?.initWindow()
    }
}
