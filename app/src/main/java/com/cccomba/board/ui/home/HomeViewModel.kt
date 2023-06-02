package com.cccomba.board.ui.home

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cccomba.board.background.view.keyboard.repository.BottomRightCharacterRepository
import com.cccomba.board.data.KeyboardTheme
import com.cccomba.board.data.NewTheme
import com.cccomba.board.data.Theme
import com.cccomba.board.data.db.ThemeDataBase
import com.cccomba.board.databinding.ItemThemeBinding
import com.cccomba.board.repository.PrefsReporitory
import com.cccomba.board.ui.base.BaseViewModel
import com.cccomba.board.ui.base.createAdapter
import com.cccomba.board.util.enums.KeyboardHeight
import com.cccomba.board.util.enums.LanguageChange
import com.cccomba.board.util.enums.OneHandedMode
import com.cccomba.board.util.themesPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {

    val onAddClick = MutableLiveData<Unit>()
    val onThemeClick = MutableLiveData<KeyboardTheme>()
    val onApplied = MutableLiveData<Unit>()

    val theme = ObservableField<KeyboardTheme?>()

    val presetSelected = ObservableBoolean(true)
    val customSelected = ObservableBoolean(false)
    val settingsSelected = ObservableBoolean(false)

    val adapterPreset = createAdapter<Theme, ViewDataBinding> {
        initItems = themesPreset
        viewBinding = { inflater, viewGroup, _ -> ItemThemeBinding.inflate(inflater, viewGroup, false) }
        onBind = onBind@ { theme, _ ->
            if (theme !is KeyboardTheme) return@onBind
            theme.isSelectedObservable.set(theme.id == PrefsReporitory.keyboardTheme?.id && theme.backgroundImagePath == PrefsReporitory.keyboardTheme?.backgroundImagePath)
        }
        onItemClick = {
            onThemeClick.postValue(it as KeyboardTheme)
            theme.set(it)
        }
    }

    val adapterCustom = createAdapter<Theme, ViewDataBinding> {
        initItems = listOf()
        viewBinding = { inflater, viewGroup, _ -> ItemThemeBinding.inflate(inflater, viewGroup, false) }
        onItemClick = {
            if (it is NewTheme) onAddClick.postValue(Unit)
            else {
                onThemeClick.postValue(it as KeyboardTheme)
                theme.set(it)
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Main) {
            val themes = withContext(Dispatchers.IO) { ThemeDataBase.dataBase.getThemesDao().getTheme().reversed() }
            themes.forEach { it.isSelectedObservable.set(it.id == PrefsReporitory.keyboardTheme?.id && it.backgroundImagePath == PrefsReporitory.keyboardTheme?.backgroundImagePath) }
            adapterCustom.addItems(themes)
        }
    }

    fun apply() {
        PrefsReporitory.keyboardTheme = theme.get()
        listOf(
            *adapterPreset.getData().toTypedArray(),
            *adapterCustom.getData().toTypedArray()
        ).mapNotNull { it as? KeyboardTheme }.forEach { it.isSelectedObservable.set(it === theme.get()) }
        onApplied.postValue(Unit)
    }

    fun onPresetClick() {
        customSelected.set(false)
        settingsSelected.set(false)
        presetSelected.set(true)
    }

    fun onCustomClick() {
        presetSelected.set(false)
        settingsSelected.set(false)
        customSelected.set(true)
    }

    fun onSettingsClick() {
        customSelected.set(false)
        presetSelected.set(false)
        settingsSelected.set(true)
    }

    fun onAddClick() = onAddClick.postValue(Unit)

    val isGlideTypingOn = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)
    val isShowEmojiOn = ObservableBoolean(PrefsReporitory.Settings.showEmoji)
    val isTipsOn = ObservableBoolean(PrefsReporitory.Settings.tips)
    val isKeyboardSwipeOn = ObservableBoolean(PrefsReporitory.Settings.keyboardSwipe)
    val isShowNumberRowOn = ObservableBoolean(PrefsReporitory.Settings.showNumberRow)
    val oneHandedMode = ObservableField(PrefsReporitory.Settings.oneHandedMode)
    val keyboardHeight = ObservableField(PrefsReporitory.Settings.keyboardHeight)
    val languageChange = ObservableField(PrefsReporitory.Settings.languageChange)
    val specialSymbol = ObservableField(BottomRightCharacterRepository.SelectableCharacter.from(PrefsReporitory.Settings.specialSymbol))

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
        keyboardHeight.set(PrefsReporitory.Settings.keyboardHeight)
        languageChange.set(PrefsReporitory.Settings.languageChange)
        specialSymbol.set(BottomRightCharacterRepository.SelectableCharacter.from(PrefsReporitory.Settings.specialSymbol))
    }

    class Factory : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeViewModel() as T

    }

}
