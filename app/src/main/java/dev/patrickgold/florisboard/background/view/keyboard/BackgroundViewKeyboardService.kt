package dev.patrickgold.florisboard.background.view.keyboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.patrickgold.florisboard.background.view.keyboard.repository.BackgroundViewRepository
import dev.patrickgold.florisboard.crashutility.CrashUtility
import dev.patrickgold.florisboard.databinding.FlorisboardBinding
import dev.patrickgold.florisboard.debug.LogTopic
import dev.patrickgold.florisboard.debug.flogInfo
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.popup.PopupLayerView
import dev.patrickgold.florisboard.util.addInto

class BackgroundViewKeyboardService : FlorisBoard() {

    @SuppressLint("InflateParams")
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

        BackgroundViewRepository.dispatchBackgroundView(themeContext)?.addInto(uiBinding!!.text.backgroundViewContainer)

        eventListeners.toList().forEach { it?.onInitializeInputUi(uiBinding!!) }

        return uiBinding!!.inputWindowView
    }

}
