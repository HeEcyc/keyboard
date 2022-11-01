package com.puddy.board.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import com.puddy.board.R
import com.puddy.board.databinding.CustomNavBarBinding

class CustomNavBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var onPageChange: ((Int) -> Unit)? = null

    val binding: CustomNavBarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_nav_bar,
        this,
        true
    )!!

    private var currentActiveView = binding.pressetsButtonLayout

    init {
        binding.presetsOverlay.setOnClickListener(this)
        binding.customOverlay.setOnClickListener(this)
        binding.settingsOverlay.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val activeView = when (v.id) {
            R.id.presetsOverlay -> binding.pressetsButtonLayout
            R.id.customOverlay -> binding.customButtonLayout
            R.id.settingsOverlay -> binding.settingsButtonLayout
            else -> return
        }

        if (activeView.id == currentActiveView.id) return

        setViewNotActive()
        currentActiveView = activeView
        changeSelectedItemIndex()
        setViewActivte()
    }

    private fun changeSelectedItemIndex() {
        when (currentActiveView.id) {
            R.id.pressetsButtonLayout -> 0
            R.id.customButtonLayout -> 1
            else -> 2
        }.let { onPageChange?.invoke(it) }
    }

    private fun setViewActivte() {
        currentActiveView.forEach {
            if (it is AppCompatImageView) {
                it.setImageResource(getActiveIcon(it))
            }
            if (it is GradientText) {
                it.setEnableGradiend(true)
                it.setEnableShadow(true)
            }
        }
    }

    private fun getActiveIcon(it: AppCompatImageView): Int {
        return when (it.id) {
            R.id.iconPresets -> R.drawable.ic_presets_active
            R.id.iconCustom -> R.drawable.ic_menu_custom_active
            else -> R.drawable.ic_settings_active
        }
    }

    private fun getNotActiveIcon(it: AppCompatImageView): Int {
        return when (it.id) {
            R.id.iconPresets -> R.drawable.ic_home
            R.id.iconCustom -> R.drawable.ic_menu_custom
            else -> R.drawable.ic_settings
        }
    }

    private fun setViewNotActive() {
        currentActiveView.forEach {
            if (it is AppCompatImageView) {
                it.setImageResource(getNotActiveIcon(it))
            }
            if (it is GradientText) {
                it.setEnableGradiend(false)
                it.setEnableShadow(false)
            }
        }
    }


    fun setCurrentPage(positon: Int) {
        val activeView = getActiveViewByPosition(positon)
        if (activeView.id == currentActiveView.id) return

        setViewNotActive()

        currentActiveView = activeView

        setViewActivte()
    }

    private fun getActiveViewByPosition(positon: Int) = when (positon) {
        0 -> binding.pressetsButtonLayout
        1 -> binding.customButtonLayout
        else -> binding.settingsButtonLayout
    }
}
