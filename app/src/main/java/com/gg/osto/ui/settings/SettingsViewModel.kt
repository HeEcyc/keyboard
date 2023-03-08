package com.gg.osto.ui.settings

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gg.osto.background.view.keyboard.repository.BottomRightCharacterRepository
import com.gg.osto.repository.PrefsReporitory
import com.gg.osto.ui.base.BaseViewModel
import com.gg.osto.util.enums.KeyboardHeight
import com.gg.osto.util.enums.LanguageChange
import com.gg.osto.util.enums.OneHandedMode

class SettingsViewModel : BaseViewModel() {

    val isGlideTypingOn = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)
    val isShowEmojiOn = ObservableBoolean(PrefsReporitory.Settings.showEmoji)
    val isTipsOn = ObservableBoolean(PrefsReporitory.Settings.tips)
    val isKeyboardSwipeOn = ObservableBoolean(PrefsReporitory.Settings.keyboardSwipe)
    val isShowNumberRowOn = ObservableBoolean(PrefsReporitory.Settings.showNumberRow)
    val oneHandedMode = ObservableField(PrefsReporitory.Settings.oneHandedMode)

    fun onGlideTypingClick() {
        PrefsReporitory.Settings.GlideTyping.enableGlideTyping = !isGlideTypingOn.get()
        updateUI()
    }

    fun onShowEmojiClick() {
        PrefsReporitory.Settings.showEmoji = !isShowEmojiOn.get()
        updateUI()
    }

    fun onTipsClick() {
        PrefsReporitory.Settings.tips = !isTipsOn.get()
        updateUI()
    }

    fun onKeyboardSwipeClick() {
        PrefsReporitory.Settings.keyboardSwipe = !isKeyboardSwipeOn.get()
        updateUI()
    }

    fun onShowNumberRowClick() {
        PrefsReporitory.Settings.showNumberRow = !isShowNumberRowOn.get()
        updateUI()
    }

    fun onOneHandedModeSelected(ohm: OneHandedMode) {
        PrefsReporitory.Settings.oneHandedMode = ohm
        updateUI()
    }

    fun onKeyboardHeightSelected(kh: KeyboardHeight) {
        PrefsReporitory.Settings.keyboardHeight = kh
        updateUI()
    }

    fun onLanguageChangeSelected(lc: LanguageChange) {
        PrefsReporitory.Settings.languageChange = lc
        updateUI()
    }

    fun onSpecialSymbolSelected(sc: BottomRightCharacterRepository.SelectableCharacter) {
        BottomRightCharacterRepository.selectedBottomRightCharacterCode = sc.code
        updateUI()
    }

    private fun updateUI() {
        isGlideTypingOn.set(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)
        isShowEmojiOn.set(PrefsReporitory.Settings.showEmoji)
        isTipsOn.set(PrefsReporitory.Settings.tips)
        isKeyboardSwipeOn.set(PrefsReporitory.Settings.keyboardSwipe)
        isShowNumberRowOn.set(PrefsReporitory.Settings.showNumberRow)
        oneHandedMode.set(PrefsReporitory.Settings.oneHandedMode)
    }

    class Factory : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>) = SettingsViewModel() as T

    }

}
