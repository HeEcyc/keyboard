package dev.patrickgold.florisboard.background.view.keyboard.views

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import kotlin.math.roundToInt

class KeyboardBackingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val desiredHeight = FlorisBoard
            .getInstanceOrNull()
            ?.uiBinding
            ?.inputView
            ?.desiredTextKeyboardViewHeight ?: MeasureSpec.getSize(heightMeasureSpec).toFloat()

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(desiredWidth.roundToInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(desiredHeight.roundToInt(), MeasureSpec.EXACTLY)
        )
    }

}
