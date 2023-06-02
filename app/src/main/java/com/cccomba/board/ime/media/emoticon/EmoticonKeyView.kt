package com.cccomba.board.ime.media.emoticon

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import com.cccomba.board.R
import com.cccomba.board.ime.core.FlorisBoard
import com.cccomba.board.ime.media.MediaInputManager
import com.cccomba.board.ime.text.keyboard.TextKeyData
import com.cccomba.board.util.getColorFromAttr

/**
 * View class for managing the rendering and the events of a single emoticon keyboard key.
 */
class EmoticonKeyView : androidx.appcompat.widget.AppCompatTextView {
    private val florisboard: FlorisBoard = FlorisBoard.getInstance()
    private val mediaInputManager: MediaInputManager = MediaInputManager.getInstance()
    var data: EmoticonKeyData? = null
        set(value) {
            field = value
            text = value?.icon
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        background = null
        gravity = Gravity.CENTER
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).apply {
            weight = 1.0f
        }
        setPadding(0, 0, 0, 0)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.key_textSize))
    }

    /**
     * Logic for handling a touch event.
     *
     * @param event The [MotionEvent] that should be processed by this view.
     * @return If this view has handled the touch event.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                setBackgroundColor(getColorFromAttr(context, R.attr.semiTransparentColor))
                florisboard.inputFeedbackManager.keyPress(TextKeyData.UNSPECIFIED)
            }
            MotionEvent.ACTION_UP -> {
                setBackgroundColor(Color.TRANSPARENT)
                val data = data
                if (data != null) {
                    mediaInputManager.sendEmoticonKeyPress(data)
                }
            }
        }
        return true
    }
}
