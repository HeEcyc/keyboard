package com.ioskey.iosboard.ime.popup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import com.ioskey.iosboard.ime.clip.ClipboardPopupManager
import com.ioskey.iosboard.ime.clip.ClipboardPopupView

/**
 * Basic helper view class which acts as a non-interactive layer view, which sits above the whole
 * input UI. Automatically rejects any touch events and passes it through to the View below.
 */
class PopupLayerView : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        background = null
        isClickable = false
        isFocusable = false
        layoutDirection = LAYOUT_DIRECTION_LTR
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        setWillNotDraw(true)
    }

    var clipboardPopupManager: ClipboardPopupManager? = null
    var intercept: ClipboardPopupView? = null
    var shouldIntercept = true

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            intercept?.run {
                val viewRect = Rect()
                getGlobalVisibleRect(viewRect)
                return when {
                    !viewRect.contains(ev.x.toInt(), ev.y.toInt()) -> {
                        clipboardPopupManager?.hide()
                        true
                    }
                    else -> false
                }
            }
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}
