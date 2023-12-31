package com.live.keyboard.ime.onehanded

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import com.live.keyboard.R
import com.live.keyboard.ime.core.FlorisBoard
import com.live.keyboard.ime.core.Preferences
import com.live.keyboard.ime.theme.Theme
import com.live.keyboard.ime.theme.ThemeManager
import com.live.keyboard.repository.PrefsReporitory
import com.live.keyboard.util.enums.OneHandedMode

class OneHandedPanel : LinearLayout, ThemeManager.OnThemeUpdatedListener {
    private var florisboard: FlorisBoard? = null
    private var themeManager: ThemeManager? = null
    private val prefs get() = Preferences.default()

    private var closeBtn: ImageButton? = null
    private var moveBtn: ImageButton? = null

    private val panelSide: OneHandedMode

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.OneHandedPanel).apply {
            panelSide = when (getString(R.styleable.OneHandedPanel_panelSide)) {
                "start" -> OneHandedMode.LEFT
                "end" -> OneHandedMode.RIGHT
                "off" -> OneHandedMode.OFF
                else -> OneHandedMode.LEFT
            }
            recycle()
        }
        orientation = VERTICAL
        gravity = Gravity.CENTER_VERTICAL
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        florisboard = FlorisBoard.getInstanceOrNull()
        themeManager = ThemeManager.defaultOrNull()

        closeBtn = findViewWithTag("one_handed_ctrl_close")
        closeBtn?.setOnClickListener {
            florisboard?.let {
                it.inputFeedbackManager.keyPress()
                PrefsReporitory.Settings.oneHandedMode = OneHandedMode.OFF
                it.updateOneHandedPanelVisibility()
            }
        }
        moveBtn = findViewWithTag("one_handed_ctrl_move")
        moveBtn?.setOnClickListener {
            florisboard?.let {
                it.inputFeedbackManager.keyPress()
                PrefsReporitory.Settings.oneHandedMode = panelSide
                it.updateOneHandedPanelVisibility()
            }
        }

        themeManager?.registerOnThemeUpdatedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        florisboard = null
        themeManager?.unregisterOnThemeUpdatedListener(this)
        themeManager = null

        closeBtn?.setOnClickListener(null)
        closeBtn = null
        moveBtn?.setOnClickListener(null)
        moveBtn = null
    }

    override fun onThemeUpdated(theme: Theme) {
        setBackgroundColor(Color.parseColor("#292E32"))
        closeBtn?.imageTintList = ColorStateList.valueOf(Color.WHITE)
        moveBtn?.imageTintList = ColorStateList.valueOf(Color.WHITE)
        closeBtn?.invalidate()
        moveBtn?.invalidate()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (Resources.getSystem().displayMetrics.widthPixels) *
            ((100 - prefs.keyboard.oneHandedModeScaleFactor) / 100.0f)
        super.onMeasure(MeasureSpec.makeMeasureSpec(width.toInt(), MeasureSpec.EXACTLY), heightMeasureSpec)
    }
}
