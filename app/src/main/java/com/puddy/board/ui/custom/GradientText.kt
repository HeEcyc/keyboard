package com.puddy.board.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.puddy.board.R


class GradientText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var isEnableGradinent = true
        set(value) {
            field = value
            invalidate()
        }

    companion object {
        @JvmStatic
        @BindingAdapter("bind:setEnableShadow")
        fun setEnableShadow(gt: GradientText, isEnableShadow: Boolean) {
            if (isEnableShadow) gt.setShadowLayer(
                20f, 0f, 0f,
                ContextCompat.getColor(gt.context, R.color.dividerColor)
            )
            else gt.setShadowLayer(
                0f, 0f, 0f,
                ContextCompat.getColor(gt.context, R.color.dividerColor)
            )
        }
    }

    override fun onDraw(canvas: Canvas?) {
        paint.shader = if (isEnableGradinent) {
            setTextColor(Color.WHITE)
            getGradientShaders()
        } else {
            setTextColor(Color.parseColor("#808183"))
            null
        }
        super.onDraw(canvas)
    }

    private fun getGradientShaders() = LinearGradient(
        0f, 0f, 0f, textSize, intArrayOf(
            Color.parseColor("#CB75FF"),
            Color.parseColor("#E24CFB")
        ), null, Shader.TileMode.CLAMP
    )

    fun setEnableGradiend(isEnableGradinent: Boolean) {
        this.isEnableGradinent = isEnableGradinent
    }

    fun setEnableShadow(isEnableShadow: Boolean) {
        if (isEnableShadow) setShadowLayer(
            20f, 0f, 0f,
            ContextCompat.getColor(context, R.color.dividerColor)
        )
        else setShadowLayer(
            0f, 0f, 0f,
            ContextCompat.getColor(context, R.color.dividerColor)
        )
    }

}
