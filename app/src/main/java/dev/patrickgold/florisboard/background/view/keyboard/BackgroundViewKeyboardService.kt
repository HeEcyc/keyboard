package dev.patrickgold.florisboard.background.view.keyboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import dev.patrickgold.florisboard.background.view.keyboard.repository.BackgroundViewRepository
import dev.patrickgold.florisboard.crashutility.CrashUtility
import dev.patrickgold.florisboard.databinding.FlorisboardBinding
import dev.patrickgold.florisboard.debug.LogTopic
import dev.patrickgold.florisboard.debug.flogInfo
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.popup.PopupLayerView
import dev.patrickgold.florisboard.util.addInto

class BackgroundViewKeyboardService : FlorisBoard() {

    private lateinit var backgroundContainer: FrameLayout
    private var backgroundView: View? = null

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        flogInfo(LogTopic.IMS_EVENTS)
        CrashUtility.handleStagedButUnhandledExceptions()

        updateThemeContext(currentThemeResId)

        popupLayerView = PopupLayerView(themeContext)
        window?.window?.findViewById<View>(android.R.id.content)?.let { content ->
            if (content is ViewGroup) {
                content.addView(popupLayerView)
            }
        }

        uiBinding = FlorisboardBinding.inflate(LayoutInflater.from(themeContext))

        backgroundContainer = uiBinding!!.text.backgroundViewContainer

        onNewBackgroundView(BackgroundViewRepository.dispatchBackgroundView(themeContext))
        uiBinding!!.text.smartbar.root.setOnTouchListener { view, event ->
            view.onTouchEvent(event)
            backgroundView?.onTouchEvent(event)
            true
        }
        uiBinding!!.text.mainKeyboardView.setOnTouchListener { view, event ->
            view.onTouchEvent(event)
            backgroundView?.onTouchEvent(event)
            true
        }

        BackgroundViewRepository.newBackgroundViews.observe(this) {
            onNewBackgroundView(it?.createView(themeContext))
        }

        eventListeners.toList().forEach { it?.onInitializeInputUi(uiBinding!!) }

        return uiBinding!!.inputWindowView
    }

    private fun onNewBackgroundView(view: View?) {
        backgroundView = view
        view?.addInto(backgroundContainer) ?: backgroundContainer.removeAllViews()
    }

}
