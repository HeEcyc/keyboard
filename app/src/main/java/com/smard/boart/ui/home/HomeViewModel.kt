package com.smard.boart.ui.home

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smard.boart.background.view.keyboard.repository.BottomRightCharacterRepository
import com.smard.boart.data.KeyboardTheme
import com.smard.boart.data.NewTheme
import com.smard.boart.data.Theme
import com.smard.boart.data.db.ThemeDataBase
import com.smard.boart.databinding.ItemThemeBinding
import com.smard.boart.repository.PrefsReporitory
import com.smard.boart.ui.base.BaseViewModel
import com.smard.boart.ui.base.createAdapter
import com.smard.boart.util.enums.KeyboardHeight
import com.smard.boart.util.enums.LanguageChange
import com.smard.boart.util.enums.OneHandedMode
import com.smard.boart.util.themesPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {

    val onAddClick = MutableLiveData<Unit>()
    val onThemeClick = MutableLiveData<KeyboardTheme>()

    val presetSelected = ObservableBoolean(true)
    val customSelected = ObservableBoolean(false)

    val isGlideTypingOn = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)
    val isShowEmojiOn = ObservableBoolean(PrefsReporitory.Settings.showEmoji)
    val isTipsOn = ObservableBoolean(PrefsReporitory.Settings.tips)
    val isKeyboardSwipeOn = ObservableBoolean(PrefsReporitory.Settings.keyboardSwipe)
    val isShowNumberRowOn = ObservableBoolean(PrefsReporitory.Settings.showNumberRow)
    val oneHandedMode = ObservableField(PrefsReporitory.Settings.oneHandedMode)

    val adapterPreset = createAdapter<Theme, ViewDataBinding> {
        initItems = themesPreset
        viewBinding = { inflater, viewGroup, _ -> ItemThemeBinding.inflate(inflater, viewGroup, false) }
        onBind = onBind@ { theme, _ ->
            if (theme !is KeyboardTheme) return@onBind
            theme.isSelectedObservable.set(theme.id == PrefsReporitory.keyboardTheme?.id && theme.backgroundImagePath == PrefsReporitory.keyboardTheme?.backgroundImagePath)
        }
        onItemClick = {
            onThemeClick.postValue(it as KeyboardTheme)
        }
    }

    val adapterCustom = createAdapter<Theme, ViewDataBinding> {
        initItems = listOf(NewTheme)
        viewBinding = { inflater, viewGroup, _ -> ItemThemeBinding.inflate(inflater, viewGroup, false) }
        onItemClick = {
            if (it is NewTheme) onAddClick.postValue(Unit)
            else onThemeClick.postValue(it as KeyboardTheme)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Main) {
            val themes = withContext(Dispatchers.IO) { ThemeDataBase.dataBase.getThemesDao().getTheme().reversed() }
            themes.forEach { it.isSelectedObservable.set(it.id == PrefsReporitory.keyboardTheme?.id && it.backgroundImagePath == PrefsReporitory.keyboardTheme?.backgroundImagePath) }
            adapterCustom.addItems(themes)
        }
    }

    fun onPresetClick() {
        customSelected.set(false)
        presetSelected.set(true)
    }

    fun onCustomClick() {
        presetSelected.set(false)
        customSelected.set(true)
    }

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

        override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeViewModel() as T

    }

}
