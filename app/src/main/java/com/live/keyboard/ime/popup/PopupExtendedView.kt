package com.live.keyboard.ime.popup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.live.keyboard.R
import com.live.keyboard.ime.keyboard.Key
import com.live.keyboard.ime.theme.Theme
import com.live.keyboard.ime.theme.ThemeManager
import com.live.keyboard.common.ViewUtils
import kotlin.math.min

class PopupExtendedView : View, ThemeManager.OnThemeUpdatedListener {
    private val themeManager: ThemeManager = ThemeManager.default()

    private val activeBackgroundDrawable: PaintDrawable = PaintDrawable().apply {
        setCornerRadius(ViewUtils.dp2px(6.0f))
    }
    private var backgroundDrawable: PaintDrawable = PaintDrawable().apply {
        setCornerRadius(ViewUtils.dp2px(6.0f))
    }
    private val labelPaint: Paint = Paint().apply {
        alpha = 255
        color = 0
        isAntiAlias = true
        isFakeBoldText = false
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.key_textSize)
        typeface = Typeface.DEFAULT
    }
    private val tldPaint: Paint = Paint().apply {
        alpha = 255
        color = 0
        isAntiAlias = true
        isFakeBoldText = false
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.key_textSize)
        typeface = Typeface.DEFAULT
    }

    val properties: Properties = Properties(
        width = resources.getDimension(R.dimen.key_width).toInt(),
        height = resources.getDimension(R.dimen.key_height).toInt(),
        xOffset = 0,
        yOffset = 0,
        gravity = Gravity.START,
        elements = mutableListOf(),
        activeElementIndex = -1,
        labelTextSize = resources.getDimension(R.dimen.key_popup_textSize),
    )
    val isShowing: Boolean
        get() = visibility == VISIBLE

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        layoutDirection = LAYOUT_DIRECTION_LTR
        visibility = GONE
        background = backgroundDrawable
        elevation = ViewUtils.dp2px(4.0f)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        themeManager.registerOnThemeUpdatedListener(this)
    }

    override fun onDetachedFromWindow() {
        themeManager.unregisterOnThemeUpdatedListener(this)
        super.onDetachedFromWindow()
    }

    override fun onThemeUpdated(theme: Theme) {
        activeBackgroundDrawable.apply {
            setTint(theme.getAttr(Theme.Attr.POPUP_BACKGROUND_ACTIVE).toSolidColor().color)
        }
        backgroundDrawable.apply {
            setTint(theme.getAttr(Theme.Attr.POPUP_BACKGROUND).toSolidColor().color)
        }
        labelPaint.color = theme.getAttr(Theme.Attr.POPUP_FOREGROUND).toSolidColor().color
        tldPaint.color = theme.getAttr(Theme.Attr.POPUP_FOREGROUND).toSolidColor().color
        if (isShowing) {
            invalidate()
        }
    }

    private fun applyProperties(keyboardView: View, anchor: Key) {
        val anchorCoords = IntArray(2)
        keyboardView.getLocationInWindow(anchorCoords)
        val anchorX = anchorCoords[0] + anchor.visibleBounds.left
        val anchorY = anchorCoords[1] + anchor.visibleBounds.top + anchor.visibleBounds.height()
        when (val lp = layoutParams) {
            is FrameLayout.LayoutParams -> lp.apply {
                width = properties.width
                height = properties.height
                setMargins(
                    anchorX + properties.xOffset,
                    anchorY + properties.yOffset,
                    0,
                    0
                )
            }
            else -> {
                layoutParams = FrameLayout.LayoutParams(properties.width, properties.height).apply {
                    setMargins(
                        anchorX + properties.xOffset,
                        anchorY + properties.yOffset,
                        0,
                        0
                    )
                }
            }
        }
        labelPaint.textSize = properties.labelTextSize
        tldPaint.textSize = properties.labelTextSize * 0.6f
        if (isShowing) {
            requestLayout()
            invalidate()
        }
    }

    fun show(keyboardView: View, anchor: Key) {
        applyProperties(keyboardView, anchor)
        visibility = VISIBLE
        requestLayout()
        invalidate()
    }

    fun hide() {
        visibility = GONE
        requestLayout()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        if (properties.elements.isEmpty() || properties.elements.first().isEmpty()) {
            return
        }
        val baseSize = properties.elements.first().size
        val elementWidth = measuredWidth / baseSize
        val elementHeight = measuredHeight / properties.elements.size

        var currentElementIndex = 0
        for ((r, row) in properties.elements.reversed().withIndex()) {
            val leftOffset = when (properties.gravity) {
                Gravity.END -> baseSize - row.size
                else -> 0
            }
            for ((e, element) in row.withIndex()) {
                val left = (e + leftOffset) * elementWidth
                val top = r * elementHeight
                if (properties.activeElementIndex == currentElementIndex) {
                    activeBackgroundDrawable.setBounds(
                        left, top, left + elementWidth, top + elementHeight
                    )
                    activeBackgroundDrawable.draw(canvas)
                }
                when (element) {
                    is Element.Label -> {
                        val label = element.label
                        if (label.isNotEmpty()) {
                            val centerX = left + elementWidth / 2.0f
                            val centerY = top + elementHeight / 2.0f + (labelPaint.textSize - labelPaint.descent()) / 2
                            canvas.drawText(label, centerX, centerY, labelPaint)
                        }
                    }
                    is Element.Tld -> {
                        val tld = element.tld
                        if (tld.isNotEmpty()) {
                            val centerX = left + elementWidth / 2.0f
                            val centerY = top + elementHeight / 2.0f + (tldPaint.textSize - tldPaint.descent()) / 2
                            canvas.drawText(tld, centerX, centerY, tldPaint)
                        }
                    }
                    is Element.Icon -> {
                        val drawable = element.icon
                        drawable.setTint(labelPaint.color)
                        val drawableSize = (min(elementWidth, elementHeight) * 0.6f).toInt()
                        val drawablePaddingLeft = ((elementWidth - drawableSize) / 2.0f).toInt()
                        val drawablePaddingTop = ((elementHeight - drawableSize) / 2.0f).toInt()
                        drawable.setBounds(
                            left + drawablePaddingLeft,
                            top + drawablePaddingTop,
                            left + drawablePaddingLeft + drawableSize,
                            top + drawablePaddingTop + drawableSize
                        )
                        drawable.draw(canvas)
                    }
                    else -> {}
                }
                currentElementIndex++
            }
        }
    }

    data class Properties(
        var width: Int,
        var height: Int,
        var xOffset: Int,
        var yOffset: Int,
        var gravity: Int,
        var elements: MutableList<MutableList<Element>>,
        var activeElementIndex: Int,
        var labelTextSize: Float
    ) {
        fun getElementOrNull(index: Int = activeElementIndex): Element? {
            if (index < 0) {
                return null
            }
            var cachedIndex = index
            elements.reversed().forEach { row ->
                if (cachedIndex >= row.size) {
                    cachedIndex -= row.size
                } else {
                    return row[cachedIndex]
                }
            }
            return null
        }
    }

    sealed class Element(val adjustedIndex: Int) {
        class Label(val label: String, adjustedIndex: Int) : Element(adjustedIndex)
        class Tld(val tld: String, adjustedIndex: Int) : Element(adjustedIndex)
        class Icon(val icon: Drawable, adjustedIndex: Int) : Element(adjustedIndex)
        object Undefined : Element(-1)
    }
}
