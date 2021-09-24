package com.live.keyboard.background.view.keyboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.live.keyboard.background.view.keyboard.repository.BackgroundViewRepository
import com.live.keyboard.crashutility.CrashUtility
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.databinding.FlorisboardBinding
import com.live.keyboard.ime.core.FlorisBoard
import com.live.keyboard.ime.popup.PopupLayerView
import com.live.keyboard.repository.PrefsReporitory

class BackgroundViewKeyboardService : FlorisBoard() {

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        CrashUtility.handleStagedButUnhandledExceptions()
        updateThemeContext(currentThemeResId)
        popupLayerView = PopupLayerView(themeContext)
        window?.window?.findViewById<View>(android.R.id.content)?.let { content ->
            if (content is ViewGroup) {
                content.addView(popupLayerView)
            }
        }

        uiBinding = FlorisboardBinding.inflate(LayoutInflater.from(themeContext))
        eventListeners.toList().forEach { it?.onInitializeInputUi(uiBinding!!) }

        return uiBinding!!.inputWindowView
    }

    override fun attachBackground(backgroundContainer: FrameLayout) {
        backgroundContainer.setBackgroundColor(Color.TRANSPARENT)
        backgroundContainer.removeAllViews()

        val keyboardTheme = PrefsReporitory.keyboardTheme ?: KeyboardTheme()

        when {
            !keyboardTheme.backgoundType.isNullOrEmpty() -> BackgroundViewRepository.BackgroundView
                .fromName(keyboardTheme.backgoundType)
            !keyboardTheme.backgroundImagePath.isNullOrEmpty() -> BackgroundViewRepository.BackgroundView
                .ImageView(Uri.parse(keyboardTheme.backgroundImagePath))
            else -> null
        }?.let { view -> attackBackground(view.getViewFactory().createView(themeContext), backgroundContainer) }

        if (!keyboardTheme.backgroundColor.isNullOrEmpty())
            backgroundContainer.setBackgroundColor(Color.parseColor(keyboardTheme.backgroundColor))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun attackBackground(backgroundView: View, backgroundContainer: ViewGroup) {
        uiBinding!!.text.mainKeyboardView.setOnTouchListener { view, event ->
            view.onTouchEvent(event)
            backgroundView.onTouchEvent(event)
            true
        }
        backgroundContainer.addView(backgroundView)
    }
}
