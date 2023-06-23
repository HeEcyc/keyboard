package com.cccomba.board.ui.edit

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.view.drawToBitmap
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cccomba.board.App
import com.cccomba.board.R
import com.cccomba.board.data.KeyboardTheme
import com.cccomba.board.data.db.ThemeDataBase
import com.cccomba.board.databinding.ItemBgBinding
import com.cccomba.board.ime.text.keyboard.TextKeyboardView
import com.cccomba.board.repository.PrefsReporitory
import com.cccomba.board.ui.base.BaseViewModel
import com.cccomba.board.ui.base.createAdapter
import com.cccomba.board.ui.home.HomeViewModel
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class EditViewModel(
    val theme: KeyboardTheme,
    val homeViewModel: HomeViewModel
) : BaseViewModel() {

    private val bgFolderNamePath = "images"

    val closeFragment = MutableLiveData<Unit>()
    val pickColor = MutableLiveData<(String) -> Unit>()
    val pickImage = MutableLiveData<Unit>()

    var currentPickerColor = "#FFFFFF"

    val tabText = ObservableBoolean(true)
    val tabKeys = ObservableBoolean(false)
    val tabBG = ObservableBoolean(false)

    val showFontPicker = ObservableBoolean(false)
    val showImagePicker = ObservableBoolean(false)

    val currentFont = ObservableField(theme.keyFont)
    val currentKeyColor = ObservableField(theme.keyTextColor)
    val currentStrokeColor = ObservableField<String?>(theme.strokeColor)
    val currentBackgroundColor = ObservableField<String?>(theme.backgroundColor)
    val currentButtonsColor = ObservableField(theme.buttonColor)
    val currentStrokeCornersRadius = ObservableField(theme.strokeRadius)
    val currentKeyboardBackground = ObservableField(theme.backgroundImagePath?.let { BackgroundAsset.BackgroundTheme(it) })
    val keyBGOpacity = ObservableField(theme.opacity)

    val adapterFonts = createAdapter<KeyboardFont, com.cccomba.board.databinding.ItemFontBinding>(R.layout.item_font) {
        initItems = getFontsList()
        onItemClick = {
            unselectOtherFonts(it)
            it.isSelected.set(true)
        }
    }

    private fun unselectOtherFonts(font: KeyboardFont) {
        adapterFonts.getData().forEach {
            if (it !== font && it.isSelected.get()) {
                it.isSelected.set(false)
                return
            }
        }
    }

    private fun getFontsList() = listOf(
        KeyboardFont("Arial", R.font.arial, R.font.arial == currentFont.get()),
        KeyboardFont("Roboto", R.font.roboto_400, R.font.roboto_400 == currentFont.get()),
        KeyboardFont("Times New Roman", R.font.times, R.font.times == currentFont.get()),
        KeyboardFont("IBM Plex Mono", R.font.ibm_plex, R.font.ibm_plex == currentFont.get()),
        KeyboardFont("Verdana", R.font.verdana, R.font.verdana == currentFont.get()),
    )

    val backgroundAdapter = createAdapter<BackgroundAsset, ItemBgBinding>(R.layout.item_bg) {
        initItems = mutableListOf<BackgroundAsset>(BackgroundAsset.NewImage)
            .apply { addAll(readAssetImages()) }
        onItemClick = {
            if (it is BackgroundAsset.BackgroundTheme) {
                unselectOtherImages(it)
                it.isSelected.set(true)
                onImagePicked()
            } else pickImage.postValue(Unit)
        }
    }

    private fun unselectOtherImages(img: BackgroundAsset) {
        backgroundAdapter.getData().forEach {
            if (it !== img && it is BackgroundAsset.BackgroundTheme && it.isSelected.get()) {
                it.isSelected.set(false)
                return
            }
        }
    }

    private fun readAssetImages(): List<BackgroundAsset> =
        App.instance.assets
            .list(bgFolderNamePath)
            ?.filter { it.startsWith("image") }
            ?.mapIndexed { i, it -> BackgroundAsset.BackgroundTheme(uriPathFromAsset(it)).apply { isSelected.set(i == 0) } }
            ?: listOf()

    private fun uriPathFromAsset(it: String) =
        Uri.fromFile(File("//android_asset/${bgFolderNamePath}/$it")).toString()

    fun getFontName(id: Int) = getFontsList().first { it.fontRes == id }.name

    fun onTextTabClick() {
        tabText.set(true)
        tabKeys.set(false)
        tabBG.set(false)
    }

    fun onKeysTabClick() {
        tabText.set(false)
        tabKeys.set(true)
        tabBG.set(false)
    }

    fun onBGTabClick() {
        tabText.set(false)
        tabKeys.set(false)
        tabBG.set(true)
    }

    fun apply() {
        PrefsReporitory.keyboardTheme = theme
        listOf(
            *homeViewModel.adapterPreset.getData().toTypedArray(),
            *homeViewModel.adapterCustom.getData().toTypedArray()
        ).mapNotNull { it as? KeyboardTheme }.forEach { it.isSelectedObservable.set(it === theme) }
        closeFragment.postValue(Unit)
    }

    fun pickFontColor() {
        currentPickerColor = currentKeyColor.get() ?: "#FFFFFF"
        pickColor.postValue { currentKeyColor.set(it) }
    }

    fun pickButtonColor() {
        currentPickerColor = currentButtonsColor.get() ?: "#FFFFFF"
        pickColor.postValue { currentButtonsColor.set(it) }
    }

    fun pickStrokeColor() {
        currentPickerColor = currentStrokeColor.get() ?: "#FFFFFF"
        pickColor.postValue { currentStrokeColor.set(it) }
    }

    fun pickBGColor() {
        currentPickerColor = currentBackgroundColor.get() ?: "#FFFFFF"
        pickColor.postValue { currentBackgroundColor.set(it) }
    }

    fun pickFont() {
        adapterFonts.getData().forEach { it.isSelected.set(it.fontRes == currentFont.get()) }
        showFontPicker.set(true)
    }

    fun onFontPicked() {
        currentFont.set(adapterFonts.getData().first { it.isSelected.get() }.fontRes)
        showFontPicker.set(false)
    }

    fun closeFontPicker() = showFontPicker.set(false)

    fun pickImage() {
        backgroundAdapter.getData().filterIsInstance(BackgroundAsset.BackgroundTheme::class.java).forEach {
            it.isSelected.set(false)
        }
        showImagePicker.set(true)
    }

    fun onImagePicked() {
        currentKeyboardBackground.set(
            backgroundAdapter.getData().firstOrNull {
                it is BackgroundAsset.BackgroundTheme && it.isSelected.get()
            } as? BackgroundAsset.BackgroundTheme ?: currentKeyboardBackground.get()
        )
        showImagePicker.set(false)
    }

    fun closeImagePicker() = showImagePicker.set(false)

    fun setCustomBackground(imagePath: String?) {
        imagePath ?: return
        currentKeyboardBackground.set(BackgroundAsset.BackgroundTheme(imagePath))
        showImagePicker.set(false)
    }

    fun saveKeyboardImage(keyboardView: TextKeyboardView, keyboardId: Long?) {
        keyboardId ?: return
        removeThemeFile(keyboardId)
        saveKeyboardPreviewFile(keyboardView.drawToBitmap(Bitmap.Config.ARGB_8888), keyboardId)
    }

    private fun removeThemeFile(keyboardId: Long) {
        File(App.instance.filesDir, "${keyboardId}.png").deleteOnExit()
    }

    private fun saveKeyboardPreviewFile(bitmap: Bitmap, keyboardId: Long) {
        BufferedOutputStream(FileOutputStream(File(App.instance.filesDir, "${keyboardId}.png")))
            .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    fun setCurrentKeyboard(keyboardTheme: KeyboardTheme) {
        keyboardTheme.id = null
        keyboardTheme.id = ThemeDataBase.dataBase.getThemesDao().insertTheme(keyboardTheme)
    }

    fun attachKeyboard(keyboardTheme: KeyboardTheme) {
        PrefsReporitory.keyboardTheme = keyboardTheme
        homeViewModel.adapterCustom.addItem(0, keyboardTheme)
        homeViewModel.adapterCustom.getData()
            .filterIsInstance(KeyboardTheme::class.java)
            .forEach { it.isSelectedObservable.set(it === keyboardTheme) }
        homeViewModel.adapterPreset.getData()
            .map { it as KeyboardTheme }
            .forEach { it.isSelectedObservable.set(false) }
    }

    class Factory(
        private val theme: KeyboardTheme,
        private val homeViewModel: HomeViewModel
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = EditViewModel(theme, homeViewModel) as T
    }

    sealed class BackgroundAsset {
        object NewImage : BackgroundAsset()
        data class BackgroundTheme(val uri: String, val backgroundTypeName: String? = null) : BackgroundAsset() {
            val isSelected = ObservableBoolean(false)
        }
    }

    class KeyboardFont(val name: String, val fontRes: Int, isSelected: Boolean) {
        val isSelected = ObservableBoolean(isSelected)
    }

}
