package com.ioskey.iosboard.ime.text.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ioskey.iosboard.common.ViewUtils

class TextKeyView : View {
    private val parentKeyboardView get() = parent as? TextKeyboardView

    var key: TextKey? = null

    val labelPaint: Paint = Paint().also {
        it.isAntiAlias = true
        it.isFakeBoldText = false
        it.textAlign = Paint.Align.CENTER
        it.typeface = Typeface.DEFAULT
    }
    val hintedLabelPaint: Paint = Paint().also {
        it.isAntiAlias = true
        it.isFakeBoldText = false
        it.textAlign = Paint.Align.CENTER
        it.typeface = Typeface.MONOSPACE
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#292e33"))
            cornerRadius = ViewUtils.dp2px(6.0f)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun onHoverEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        return // This view does not measure itself
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val key = key ?: return
        canvas.save()
        canvas.translate(-x, -y)
        parentKeyboardView?.onDrawComputedKey(canvas, key, this)
        parentKeyboardView?.onDrawStroke(canvas, key)
        canvas.restore()
    }
}
