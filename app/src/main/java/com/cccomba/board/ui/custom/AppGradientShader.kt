package com.cccomba.board.ui.custom

import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.PorterDuff.Mode
import android.graphics.RadialGradient
import androidx.core.graphics.transform

class AppGradientShader (viewWidth: Float, viewHeight: Float) : ComposeShader(
    //dst
    object : RadialGradient(
        viewWidth.times(0.1164f),
        viewHeight.times(0.0821f),
        viewHeight.times(0.6902f),
        Color.parseColor("#B5FFFFFF"),
        Color.parseColor("#00FFFFFF"),
        TileMode.CLAMP
    ) {
        private val sx = viewWidth / viewHeight
        private val tx = if (sx <= 1) 0f else (viewWidth * sx - viewWidth) * -0.1164f
        init {
            transform {}
        }
        override fun getLocalMatrix(localM: Matrix): Boolean {
            val res = super.getLocalMatrix(localM)
            localM.setScale(sx, 1f)
            localM.postTranslate(tx, 0f)
            return res
        }
    },
    //src
    LinearGradient(
        0f,
        0f,
        0f,
        viewHeight,
        Color.parseColor("#15AEFC"),
        Color.parseColor("#4754DE"),
        TileMode.CLAMP
    ),
    //^
    //|
    Mode.DST_OVER
)
