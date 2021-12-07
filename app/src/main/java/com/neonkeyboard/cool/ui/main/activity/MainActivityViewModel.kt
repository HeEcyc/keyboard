package com.neonkeyboard.cool.ui.main.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.neonkeyboard.cool.NeonApplication
import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.adapters.VPAdapter
import com.neonkeyboard.cool.background.view.keyboard.repository.BottomRightCharacterRepository
import com.neonkeyboard.cool.data.KeyboardTheme
import com.neonkeyboard.cool.data.NewTheme
import com.neonkeyboard.cool.data.Theme
import com.neonkeyboard.cool.data.db.ThemeDataBase
import com.neonkeyboard.cool.databinding.ItemKeyboardNewBinding
import com.neonkeyboard.cool.databinding.ItemKeyboardThemeBinding
import com.neonkeyboard.cool.repository.PrefsReporitory
import com.neonkeyboard.cool.ui.base.BaseActivity
import com.neonkeyboard.cool.ui.base.BaseViewModel
import com.neonkeyboard.cool.ui.base.createAdapter
import com.neonkeyboard.cool.ui.custom.ThemesItemDecoration
import com.neonkeyboard.cool.ui.language.selector.activity.LanguageSelectorActivity
import com.neonkeyboard.cool.ui.theme.editor.activity.ThemeEditorActivity
import com.neonkeyboard.cool.util.SingleLiveData
import com.neonkeyboard.cool.util.enums.KeyboardHeight
import com.neonkeyboard.cool.util.enums.LanguageChange
import com.neonkeyboard.cool.util.enums.OneHandedMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(val adapter: VPAdapter) : BaseViewModel() {

    val selectSwipeSpeedEvents = MutableLiveData<Unit>()

    val onThemeClick = SingleLiveData<KeyboardTheme>()
    val nextActivity = SingleLiveData<Class<out BaseActivity<*, *>>>()
    val currentPage = ObservableField(0)
    val enableGestureCursorControl = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGestureCursorControl)

    val keyboardItemDecoration = ThemesItemDecoration(2, 25)
    val assetsThemeAdapter = createAdapter<KeyboardTheme, ItemKeyboardThemeBinding>(R.layout.item_keyboard_theme) {
        onItemClick = onThemeClick::postValue
    }

    val customThemeAdapter = createAdapter<Theme, ViewDataBinding> {
        initItems = listOf(NewTheme)
        viewBinding = { inflater, viewGroup, viewType -> getViewBinding(inflater, viewGroup, viewType) }
        itemViewTypeProvider = ::getThemeViewType
        onItemClick = {
            if (it is NewTheme) showCreateThemeActivity()
            else onThemeClick.postValue(it as KeyboardTheme)
        }
    }

    private fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup, viewType: Int) = when (viewType) {
        0 -> ItemKeyboardNewBinding.inflate(inflater, viewGroup, false)
        else -> ItemKeyboardThemeBinding.inflate(inflater, viewGroup, false)
    }

    private fun getThemeViewType(theme: Theme) = when (theme) {
        is NewTheme -> 0
        else -> 1
    }

    val showEmoji = ObservableBoolean(PrefsReporitory.Settings.showEmoji)
    val tips = ObservableBoolean(PrefsReporitory.Settings.tips)
    val keyboardSwipe = ObservableBoolean(PrefsReporitory.Settings.keyboardSwipe)
    val showNumberRow = ObservableBoolean(PrefsReporitory.Settings.showNumberRow)
    val oneHandedMode = ObservableInt(PrefsReporitory.Settings.oneHandedMode.displayName)
    val isEnableGlideTyping = ObservableBoolean(PrefsReporitory.Settings.GlideTyping.enableGlideTyping)

    init {
        observe(showEmoji) { _, _ ->
            PrefsReporitory.Settings.showEmoji = showEmoji.get()
        }
        observe(tips) { _, _ ->
            PrefsReporitory.Settings.tips = tips.get()
        }
        observe(enableGestureCursorControl) { _, _ ->
            PrefsReporitory.Settings.GlideTyping.enableGestureCursorControl = enableGestureCursorControl.get()
        }
        observe(keyboardSwipe) { _, _ ->
            PrefsReporitory.Settings.keyboardSwipe = keyboardSwipe.get()
            if (keyboardSwipe.get()) {
                isEnableGlideTyping.set(false)
                PrefsReporitory.Settings.GlideTyping.enableGlideTyping = false
            }
        }
        observe(showNumberRow) { _, _ ->
            PrefsReporitory.Settings.showNumberRow = showNumberRow.get()
        }
        observe(PrefsReporitory.Settings.oneHandedModeObservable) { _, _ ->
            oneHandedMode.set(PrefsReporitory.Settings.oneHandedMode.displayName)
        }
    }

    fun onOneHandedModeClick() = showChooserWithCallback(
        R.string.one_handed_mode,
        OneHandedMode.values(),
        PrefsReporitory.Settings.oneHandedMode,
        { item, context -> context.getString(item.displayName) }
    ) {
        PrefsReporitory.Settings.oneHandedMode = it
        oneHandedMode.set(it.displayName)
    }

    fun onKeyboardHeightClick() = showChooserWithCallback(
        R.string.keyboard_height,
        KeyboardHeight.values(),
        PrefsReporitory.Settings.keyboardHeight,
        { item, context -> context.getString(item.displayName) }
    ) {
        PrefsReporitory.Settings.keyboardHeight = it
        oneHandedMode.set(PrefsReporitory.Settings.oneHandedMode.displayName)
    }

    fun onLanguageChangeClick() = showChooserWithCallback(
        R.string.language_change,
        LanguageChange.values(),
        PrefsReporitory.Settings.languageChange,
        { item, context -> context.getString(item.displayName) }
    ) {
        if (it == LanguageChange.SWIPE_THROUGH_SPACE) enableGestureCursorControl.set(false)
        PrefsReporitory.Settings.languageChange = it
    }

    fun onSpecialSymbolsEditorClick() = showChooserWithCallback(
        R.string.special_symbols_editor,
        BottomRightCharacterRepository.SelectableCharacter.values(),
        BottomRightCharacterRepository.SelectableCharacter.from(BottomRightCharacterRepository.selectedBottomRightCharacterCode),
        { item, context -> context.getString(item.displayName) }
    ) { BottomRightCharacterRepository.selectedBottomRightCharacterCode = it.code }

    fun onMinimumSwipeSpeedClick() = selectSwipeSpeedEvents.postValue(Unit)


    fun loadAssetsThemes() {
        viewModelScope.launch(Dispatchers.IO) {
            val gson = Gson()
            val assetFolder = "ime/theme"
            val assets = NeonApplication.instance.assets

            val themeList = assets.list(assetFolder)
                ?.map { assets.open("${assetFolder}/$it").bufferedReader().use { theme -> theme.readText() } }
                ?.map { gson.fromJson(it, KeyboardTheme::class.java) }
                ?.sortedBy { it.index } ?: return@launch

            if (PrefsReporitory.keyboardTheme?.id == null) themeList.forEach {
                it.isSelected = it.backgroundImagePath == PrefsReporitory.keyboardTheme?.backgroundImagePath
            }

            withContext(Dispatchers.Main) { assetsThemeAdapter.reloadData(themeList) }
        }
    }

    fun loadSavedThemes() {
        viewModelScope.launch(Dispatchers.Main) {
            val themes = withContext(Dispatchers.IO) { ThemeDataBase.dataBase.getThemesDao().getTheme().reversed() }
            themes.forEach { it.isSelected = it.id == PrefsReporitory.keyboardTheme?.id }
            customThemeAdapter.addItems(themes)
        }
    }

    class Factory(private val adapter: VPAdapter) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>) = MainActivityViewModel(adapter) as T

    }

    fun showLanguageSettingsActivity() {
        nextActivity.postValue(LanguageSelectorActivity::class.java)
    }

    private fun showCreateThemeActivity() {
        nextActivity.postValue(ThemeEditorActivity::class.java)
    }

    fun checkEnableKeyboardSwipe() {
        if (PrefsReporitory.Settings.GlideTyping.enableGlideTyping) {
            keyboardSwipe.set(false)
            isEnableGlideTyping.set(true)
        }
    }

    fun onThemeApply(keyboardTheme: KeyboardTheme) {
        clearSelectedItem()
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            withContext(Dispatchers.Main) {
                keyboardTheme.isSelected = true
                customThemeAdapter
                    .getData()
                    .filterIsInstance(KeyboardTheme::class.java)
                    .firstOrNull { it.id == keyboardTheme.id }
                    ?.let { currentTheme ->
                        currentTheme.isSelected = true
                        currentTheme.copyTheme(keyboardTheme)
                        customThemeAdapter.updateItem(currentTheme)
                    } ?: customThemeAdapter.addItem(1, keyboardTheme)
            }
        }
    }

    fun setupKeyboard(keyboardTheme: KeyboardTheme) {
        PrefsReporitory.keyboardTheme = keyboardTheme
    }

    private fun clearSelectedItem() {
        assetsThemeAdapter.getData().firstOrNull { it.isSelected }?.let {
            it.isSelected = false
            assetsThemeAdapter.updateItem(it)
            return
        }
        customThemeAdapter.getData()
            .filterIsInstance(KeyboardTheme::class.java)
            .firstOrNull { it.isSelected }?.let {
                it.isSelected = false
                customThemeAdapter.updateItem(it)
            }
    }

    fun setSelectedItem(keyboardTheme: KeyboardTheme) {
        clearSelectedItem()
        keyboardTheme.isSelected = true
        assetsThemeAdapter.updateItem(keyboardTheme)
        customThemeAdapter.updateItem(keyboardTheme)
    }
}
