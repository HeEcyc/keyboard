package com.live.keyboard.background.view.keyboard

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.live.keyboard.background.view.keyboard.repository.BackgroundViewRepository
import com.live.keyboard.crashutility.CrashUtility
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.databinding.FlorisboardBinding
import com.live.keyboard.ime.core.FlorisBoard
import com.live.keyboard.ime.core.Subtype
import com.live.keyboard.ime.popup.PopupLayerView
import com.live.keyboard.ime.text.key.KeyCode
import com.live.keyboard.repository.PrefsReporitory
import com.live.keyboard.util.enums.Language
import com.live.keyboard.util.enums.LanguageChange

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

    override fun onWindowHidden() {
        hideSubtypeChangerView()
        super.onWindowHidden()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String) {
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

    fun getCurrentLanguage() = activeSubtype.locale.language.let(Language::from)

    override fun showSubTypeChangerView() {
        if (PrefsReporitory.Settings.languageChange == LanguageChange.SPECIAL_BUTTON) return
        val subtypeChangerView = uiBinding?.text?.subtypeChangerView ?: return
        val spaceView = uiBinding?.text?.mainKeyboardView?.findKeyViewByCode(KeyCode.SPACE) ?: return

        subtypeChangerView.layoutParams.height = (spaceView.height * 1.1f).toInt()
        subtypeChangerView.layoutParams.width = (spaceView.width * 1.2f).toInt()

        subtypeChangerView.y = spaceView.y - spaceView.height - 20
        subtypeChangerView.x = spaceView.x - ((subtypeChangerView.layoutParams.width - spaceView.width) / 2)

        subtypeChangerView.showViewPager(getLanguanges(), activeSubtype)

        if (subtypeChangerView.onPageSelected == null) {
            subtypeChangerView.onPageSelected = { subtype ->
                if (subtypeManager.getActiveSubtype()?.id != subtype.id) {
                    activeSubtype = subtype
                    onSubtypeChanged(subtype, true)
                    subtypeManager.setActiveSubtype(subtype.id)
                }
            }
        }
        subtypeChangerView.visibility = View.VISIBLE
    }

    override fun onEvent(event: MotionEvent) {
        val subtypeChangerView = uiBinding?.text?.subtypeChangerView ?: return
        if (subtypeChangerView.visibility == View.VISIBLE) subtypeChangerView
            .onEvent(event)
    }

    private fun getLanguanges(): List<Subtype> {
        return subtypeManager.subtypes
    }

    override fun hideSubtypeChangerView() {
        uiBinding?.text?.subtypeChangerView?.hide()
    }
}
