package com.live.keyboard.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.live.keyboard.R


class GradientText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var isEnableGradinent = true
        set(value) {
            field = value
            invalidate()
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
