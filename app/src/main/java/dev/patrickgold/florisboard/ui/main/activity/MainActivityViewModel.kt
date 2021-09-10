package dev.patrickgold.florisboard.ui.main.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dev.patrickgold.florisboard.FlorisApplication
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.adapters.VPAdapter
import dev.patrickgold.florisboard.background.view.keyboard.repository.BottomRightCharacterRepository
import dev.patrickgold.florisboard.data.KeyboardTheme
import dev.patrickgold.florisboard.data.NewTheme
import dev.patrickgold.florisboard.data.Theme
import dev.patrickgold.florisboard.data.db.ThemeDataBase
import dev.patrickgold.florisboard.databinding.ItemKeyboardNewBinding
import dev.patrickgold.florisboard.databinding.ItemKeyboardThemeBinding
import dev.patrickgold.florisboard.ime.core.Subtype
import dev.patrickgold.florisboard.ime.keyboard.ComputingEvaluator
import dev.patrickgold.florisboard.ime.keyboard.DefaultComputingEvaluator
import dev.patrickgold.florisboard.ime.keyboard.KeyData
import dev.patrickgold.florisboard.ime.text.key.CurrencySet
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardMode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyboardIconSet
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
import dev.patrickgold.florisboard.util.enums.KeyboardHeight
import dev.patrickgold.florisboard.util.enums.LanguageChange
import dev.patrickgold.florisboard.util.enums.OneHandedMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(val adapter: VPAdapter) : BaseViewModel() {

    val onThemeClick = SingleLiveData<KeyboardTheme>()
    val nextActivity = SingleLiveData<Class<out BaseActivity<*, *>>>()
    val currentPage = ObservableField(0)

    val keyboardItemDecoration = ThemesItemDecoration(2, 10)
    val assetsThemeAdapter = createAdapter<KeyboardTheme, ItemKeyboardThemeBinding>(R.layout.item_keyboard_theme) {
        onItemClick = onThemeClick::postValue
        onBind = { theme, binding -> syncKeyboard(binding.keyboard, theme) }
    }

    private val keyboardIconSet = TextKeyboardIconSet.new(FlorisApplication.instance)
    private val textComputingEvaluator = object : ComputingEvaluator by DefaultComputingEvaluator {
        override fun evaluateVisible(data: KeyData): Boolean {
            return data.code != KeyCode.SWITCH_TO_MEDIA_CONTEXT
        }

        override fun isSlot(data: KeyData): Boolean {
            return CurrencySet.isCurrencySlot(data.code)
        }

        override fun getSlotData(data: KeyData): KeyData {
            return TextKeyData(label = ".")
        }
    }

    val customThemeAdapter = createAdapter<Theme, ViewDataBinding> {
        initItems = listOf(NewTheme)
        viewBinding = { inflater, viewGroup, viewType -> getViewBinding(inflater, viewGroup, viewType) }
        itemViewTypeProvider = ::getThemeViewType
        onBind = { theme, binding ->
            if (binding is ItemKeyboardThemeBinding)
                syncKeyboard(binding.keyboard, theme as KeyboardTheme)
        }
        onItemClick = {
            if (it is NewTheme) showCreateThemeActivity()
            else onThemeClick.postValue(it as KeyboardTheme)
        }
    }

    private fun syncKeyboard(textKeyboardView: TextKeyboardView, keyboardTheme: KeyboardTheme) {
        textKeyboardView.setIconSet(keyboardIconSet)
        textKeyboardView.setComputingEvaluator(textComputingEvaluator)
        viewModelScope.launch {
            textKeyboardView.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(KeyboardMode.CHARACTERS, Subtype.DEFAULT).await(),
                keyboardTheme
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
            PrefsReporitory.Settings.GlideTyping.enableGlideTyping = !keyboardSwipe.get()
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
        viewModelScope.launch(Dispatchers.IO) {
            val gson = Gson()
            val assetFolder = "ime/theme"
            val assets = FlorisApplication.instance.assets

            val themeList = assets.list(assetFolder)
                ?.map { assets.open("${assetFolder}/$it").bufferedReader().use { theme -> theme.readText() } }
                ?.map { gson.fromJson(it, KeyboardTheme::class.java) } ?: return@launch

            withContext(Dispatchers.Main) { assetsThemeAdapter.reloadData(themeList) }
        }
    }

    fun loadSavedThemes() {
        viewModelScope.launch(Dispatchers.Main) {
            val themes = withContext(Dispatchers.IO) { ThemeDataBase.dataBase.getThemesDao().getTheme().reversed() }
            customThemeAdapter.addItems(themes)
        }
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

    fun onThemeApply(keyboardTheme: KeyboardTheme?) {
        keyboardTheme ?: return
        PrefsReporitory.keyboardTheme = keyboardTheme
        keyboardTheme.id = ThemeDataBase.dataBase.getThemesDao().insertTheme(keyboardTheme)
        customThemeAdapter
            .getData()
            .firstOrNull { it is KeyboardTheme && it.id == keyboardTheme.id }
            ?.let { currentTheme ->
                currentTheme as KeyboardTheme
                currentTheme.copyTheme(keyboardTheme)
                customThemeAdapter.updateItem(currentTheme)
            } ?: customThemeAdapter.addItem(1, keyboardTheme)
    }


    fun checkEnableKeyboardSwipe() {
        if (PrefsReporitory.Settings.keyboardSwipe != keyboardSwipe.get()) {
            keyboardSwipe.set(PrefsReporitory.Settings.keyboardSwipe)
        }
    }
}
