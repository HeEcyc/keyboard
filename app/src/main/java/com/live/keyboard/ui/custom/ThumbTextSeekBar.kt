package com.live.keyboard.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import com.live.keyboard.R
import com.live.keyboard.databinding.CustomThumbTextSeekBarBinding
import com.live.keyboard.util.dpToPx

class ThumbTextSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val seekBar by lazy { binding.progress }
    private val seekBarWidth by lazy { seekBar.width - seekBar.paddingLeft - seekBar.paddingRight }

    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        textSize = context.dpToPx(18).toFloat()
    }
    var onProgress: ((Int) -> Unit)? = null
    val progress get() = binding.progress.progress

    val binding: CustomThumbTextSeekBarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.custom_thumb_text_seek_bar, this, true
    )

    init {
        setWillNotDraw(false)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                onProgress?.invoke(progress)
                invalidate()
            }
        })

        seekBar.thumb = ShapeDrawable(OvalShape()).apply {
            paint.color = Color.BLACK
            intrinsicHeight = context.dpToPx(25)
            intrinsicWidth = context.dpToPx(25)
        }
    }


    fun setProgress(progress: Int?) {
        progress ?: return
        binding.progress.progress = progress
    }

    fun setMax(max: Int?) {
        max ?: return
        binding.progress.max = max
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(progress.toString(), getThumbPos().toFloat(), height / 3f, textPaint)
    }

    private fun getThumbPos() =
        seekBar.paddingLeft + seekBarWidth * seekBar.progress / seekBar.max
}
