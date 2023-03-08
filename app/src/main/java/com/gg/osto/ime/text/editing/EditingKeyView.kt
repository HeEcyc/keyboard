package com.gg.osto.ime.text.editing

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import com.gg.osto.R
import com.gg.osto.ime.core.FlorisBoard
import com.gg.osto.ime.core.InputKeyEvent
import com.gg.osto.ime.core.Preferences
import com.gg.osto.ime.text.key.KeyCode
import com.gg.osto.ime.text.keyboard.TextKeyData
import com.gg.osto.ime.theme.Theme
import com.gg.osto.ime.theme.ThemeManager
import com.gg.osto.ime.theme.ThemeValue
import com.gg.osto.util.cancelAll
import com.gg.osto.util.postAtScheduledRate

/**
 * View class for managing and rendering an editing key.
 */
class EditingKeyView : AppCompatImageButton, ThemeManager.OnThemeUpdatedListener {
    private val florisboard: FlorisBoard? = FlorisBoard.getInstanceOrNull()
    private val prefs get() = Preferences.default()
    private val themeManager: ThemeManager = ThemeManager.default()
    private val data: TextKeyData = when (id) {
        else -> TextKeyData.UNSPECIFIED
    }
    private var isKeyPressed: Boolean = false
    private val repeatedKeyPressHandler: Handler = Handler(context.mainLooper)

    private val defaultTextSize: Float = Button(context).textSize
    private var label: String? = null
    private var labelPaint: Paint = Paint().apply {
        alpha = 255
        color = 0
        isAntiAlias = true
        isFakeBoldText = false
        textAlign = Paint.Align.CENTER
        textSize = defaultTextSize
        typeface = Typeface.DEFAULT
    }

    private var colorHighlightedEnabled: ThemeValue = ThemeValue.SolidColor(0)
    private var colorEnabled: ThemeValue = ThemeValue.SolidColor(0)
    private var colorDefault: ThemeValue = ThemeValue.SolidColor(0)

    var isHighlighted: Boolean = false
        set(value) {
            field = value; invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.style.TextEditingButton)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.EditingKeyView).apply {
            label = getString(R.styleable.EditingKeyView_android_text)
            recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        themeManager.registerOnThemeUpdatedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        themeManager.unregisterOnThemeUpdatedListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled || event == null) {
            return false
        }
        super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isKeyPressed = true
                florisboard?.inputFeedbackManager?.keyPress(data)
                when (data.code) {
                    KeyCode.ARROW_DOWN,
                    KeyCode.ARROW_LEFT,
                    KeyCode.ARROW_RIGHT,
                    KeyCode.ARROW_UP,
                    KeyCode.DELETE -> {
                        val delayMillis = prefs.keyboard.longPressDelay.toLong()
                        repeatedKeyPressHandler.postAtScheduledRate(delayMillis, 25) {
                            if (isKeyPressed) {
                                florisboard?.textInputManager?.inputEventDispatcher?.send(InputKeyEvent.downUp(data))
                            } else {
                                repeatedKeyPressHandler.cancelAll()
                            }
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isKeyPressed = false
                repeatedKeyPressHandler.cancelAll()
                if (event.actionMasked != MotionEvent.ACTION_CANCEL) {
                    florisboard?.textInputManager?.inputEventDispatcher?.send(InputKeyEvent.downUp(data))
                }
            }
            else -> return false
        }
        return true
    }

    override fun onThemeUpdated(theme: Theme) {
        imageTintList = ColorStateList.valueOf(
            when {
                isEnabled -> theme.getAttr(Theme.Attr.SMARTBAR_FOREGROUND).toSolidColor().color
                else -> theme.getAttr(Theme.Attr.SMARTBAR_FOREGROUND_ALT).toSolidColor().color
            }
        )
        colorHighlightedEnabled = theme.getAttr(Theme.Attr.WINDOW_COLOR_PRIMARY)
        colorEnabled = theme.getAttr(Theme.Attr.SMARTBAR_FOREGROUND_ALT)
        colorDefault = theme.getAttr(Theme.Attr.SMARTBAR_FOREGROUND)
    }

    /**
     * Draw the key label / drawable.
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        // Draw label
        val label = label
        if (label != null) {
            labelPaint.color = if (isHighlighted && isEnabled) {
                colorHighlightedEnabled.toSolidColor().color
            } else if (!isEnabled) {
                colorEnabled.toSolidColor().color
            } else {
                colorDefault.toSolidColor().color
            }
            val isPortrait =
                resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            labelPaint.textSize = if (isPortrait) {
                defaultTextSize
            } else {
                defaultTextSize * 0.9f
            }
            val centerX = measuredWidth / 2.0f
            val centerY = measuredHeight / 2.0f + (labelPaint.textSize - labelPaint.descent()) / 2
            if (label.contains("\n")) {
                // Even if more lines may be existing only the first 2 are shown
                val labelLines = label.split("\n")
                canvas.drawText(labelLines[0], centerX, centerY * 0.70f, labelPaint)
                canvas.drawText(labelLines[1], centerX, centerY * 1.30f, labelPaint)
            } else {
                canvas.drawText(label, centerX, centerY, labelPaint)
            }
        }
    }
}
