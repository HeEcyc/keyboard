package com.live.keyboard.background.view.keyboard

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.live.keyboard.background.view.keyboard.repository.BackgroundViewRepository
import com.live.keyboard.crashutility.CrashUtility
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.databinding.FlorisboardBinding
import com.live.keyboard.ime.core.FlorisBoard
import com.live.keyboard.ime.popup.PopupLayerView
import com.live.keyboard.repository.PrefsReporitory

class BackgroundViewKeyboardService : FlorisBoard(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val keyboardTheme get() = PrefsReporitory.keyboardTheme ?: KeyboardTheme()

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        PrefsReporitory.subscribeToPrefsChangeListener(this)
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

    override fun attachBackground() {
        when {
            !keyboardTheme.backgoundType.isNullOrEmpty() -> BackgroundViewRepository.BackgroundView
                .fromName(keyboardTheme.backgoundType)
            !keyboardTheme.backgroundImagePath.isNullOrEmpty() ->
                if (bgContainer.childCount == 0) BackgroundViewRepository.BackgroundView
                    .ImageView(Uri.parse(keyboardTheme.backgroundImagePath))
                else null
            else -> {
                if (!keyboardTheme.backgroundColor.isNullOrEmpty()) {
                    bgContainer.removeAllViews()
                    bgContainer.setBackgroundColor(Color.parseColor(keyboardTheme.backgroundColor))
                }
                null
            }
        }?.let { view ->
            attackBackground(view.getViewFactory().createView(themeContext))
            view
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun attackBackground(backgroundView: View) {
        bgContainer.removeAllViews()
        uiBinding!!.text.mainKeyboardView.setOnTouchListener { view, event ->
            view.onTouchEvent(event)
            backgroundView.onTouchEvent(event)
            true
        }
        bgContainer.addView(backgroundView)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String) {
        Log.d("12345", "enter")
        if (p1 != PrefsReporitory.keyboadThemeKey) return
        when {
            !keyboardTheme.backgoundType.isNullOrEmpty() -> null
            !keyboardTheme.backgroundImagePath.isNullOrEmpty() -> BackgroundViewRepository.BackgroundView
                .ImageView(Uri.parse(keyboardTheme.backgroundImagePath))
            else -> null
        }?.let { view ->
            attackBackground(view.getViewFactory().createView(themeContext))
        }
    }
}
