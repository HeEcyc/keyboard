package com.gg.osto.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

class TrueAutoSizeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var fontToHeightRatio = 0.5f
        set(value) {
            field = value
            requestLayout()
        }

    companion object {
        @JvmStatic
        @BindingAdapter("fontToHeightRatio")
        fun setFontToHeightRatio(tastv: TrueAutoSizeTextView, fthr: Float) {
            tastv.fontToHeightRatio = fthr
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textSize =
            MeasureSpec
                .getSize(heightMeasureSpec) / resources.displayMetrics.density * fontToHeightRatio.times(0.8f)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}
