package dev.patrickgold.florisboard.ui.main.activity

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.adapters.VPAdapter
import dev.patrickgold.florisboard.data.NewTheme
import dev.patrickgold.florisboard.data.Theme
import dev.patrickgold.florisboard.databinding.ItemKeyboardNewBinding
import dev.patrickgold.florisboard.background.view.keyboard.repository.BottomRightCharacterRepository
import dev.patrickgold.florisboard.databinding.ItemKeyboardThemeBinding
import dev.patrickgold.florisboard.ime.core.Subtype
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardMode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyboardView
import dev.patrickgold.florisboard.ime.text.layout.LayoutManager
import dev.patrickgold.florisboard.repository.PrefsReporitory
import dev.patrickgold.florisboard.ui.base.BaseActivity
import dev.patrickgold.florisboard.ui.base.BaseViewModel
import dev.patrickgold.florisboard.ui.base.createAdapter
import dev.patrickgold.florisboard.ui.custom.ThemesItemDecoration
import dev.patrickgold.florisboard.ui.glide.preferences.activity.GlideTypingPreferenceActivity
import dev.patrickgold.florisboard.ui.language.selector.activity.LanguageSelectorActivity
import dev.patrickgold.florisboard.ui.theme.editor.activity.ThemeEditorActivity
import dev.patrickgold.florisboard.util.SingleLiveData
import kotlinx.coroutines.launch
import dev.patrickgold.florisboard.util.enums.KeyboardHeight
import dev.patrickgold.florisboard.util.enums.LanguageChange
import dev.patrickgold.florisboard.util.enums.OneHandedMode

class MainActivityViewModel(val adapter: VPAdapter) : BaseViewModel() {

    val onThemeClick = SingleLiveData<String>()
    val nextActivity = SingleLiveData<Class<out BaseActivity<*, *>>>()
    val currentPage = ObservableField(0)

    val keyboardItemDecoration = ThemesItemDecoration(2, 10)
    val assetsThemeAdapter = createAdapter<String, ItemKeyboardThemeBinding>(R.layout.item_keyboard_theme) {
        initItems = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "")
        onItemClick = {
            onThemeClick.postValue("")
        }
        onBind = { _, binding -> syncKeyboard(binding.keyboard) }
    }
    val customThemeAdapter = createAdapter<Theme, ViewDataBinding>(R.layout.item_keyboard_theme) {
        initItems = listOf(NewTheme)
        viewBinding = { inflater, viewGroup, viewType -> getViewBinding(inflater, viewGroup, viewType) }
        itemViewTypeProvider = ::getThemeViewType
        onItemClick = {
            if (it is NewTheme) showCreateThemeActivity()
            else onThemeClick::postValue
        }
    }

    init {
        Log.d("12345", "12345")
    }

    private fun syncKeyboard(textKeyboardView: TextKeyboardView) {
        Log.d("12345", "bind")
        viewModelScope.launch {

            textKeyboardView.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(
                    KeyboardMode.CHARACTERS,
                    Subtype.DEFAULT
                ).await()
            )
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

    init {
        observe(showEmoji) { _, _ ->
            PrefsReporitory.Settings.showEmoji = showEmoji.get()
        }
        observe(tips) { _, _ ->
            PrefsReporitory.Settings.tips = tips.get()
        }
        observe(keyboardSwipe) { _, _ ->
            PrefsReporitory.Settings.keyboardSwipe = keyboardSwipe.get()
        }
        observe(showNumberRow) { _, _ ->
            PrefsReporitory.Settings.showNumberRow = showNumberRow.get()
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
    ) { PrefsReporitory.Settings.keyboardHeight = it }

    fun onLanguageChangeClick() = showChooserWithCallback(
        R.string.language_change,
        LanguageChange.values(),
        PrefsReporitory.Settings.languageChange,
        { item, context -> context.getString(item.displayName) }
    ) { PrefsReporitory.Settings.languageChange = it }

    fun onSpecialSymbolsEditorClick() = showChooserWithCallback(
        R.string.special_symbols_editor,
        BottomRightCharacterRepository.SelectableCharacter.values(),
        BottomRightCharacterRepository.SelectableCharacter.from(BottomRightCharacterRepository.selectedBottomRightCharacterCode),
        { item, context -> context.getString(item.displayName) }
    ) { BottomRightCharacterRepository.selectedBottomRightCharacterCode = it.code }


    fun loadAssetsThemes() {

    }

    fun loadSavedThemes() {

    }

    class Factory(private val adapter: VPAdapter) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>) = MainActivityViewModel(adapter) as T

    }

    fun showLanguageSettingsActivity() {
        nextActivity.postValue(LanguageSelectorActivity::class.java)
    }

    fun showGlideSettingsActivity() {
        nextActivity.postValue(GlideTypingPreferenceActivity::class.java)
    }

    private fun showCreateThemeActivity() {
        nextActivity.postValue(ThemeEditorActivity::class.java)
    }
}
