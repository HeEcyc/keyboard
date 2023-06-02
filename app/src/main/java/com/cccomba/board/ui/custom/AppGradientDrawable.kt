package com.cccomba.board.ui.custom

import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.shapes.RectShape

class AppGradientDrawable : PaintDrawable() {
    init {
        shape = RectShape()
        shaderFactory = object : ShaderFactory() {
            override fun resize(width: Int, height: Int) = AppGradientShader(width.toFloat(), height.toFloat())
        }
    }
}
