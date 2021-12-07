package com.neonkeyboard.cool.ui.theme.editor.activity

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import com.neonkeyboard.cool.NeonApplication
import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.data.KeyboardTheme
import com.neonkeyboard.cool.data.db.ThemeDataBase
import com.neonkeyboard.cool.databinding.ItemColorBinding
import com.neonkeyboard.cool.databinding.ItemColorNewBinding
import com.neonkeyboard.cool.databinding.ItemFontBinding
import com.neonkeyboard.cool.databinding.ItemKeyboardBackgroundAssetBinding
import com.neonkeyboard.cool.databinding.ItemKeyboardBackgroundNewBinding
import com.neonkeyboard.cool.databinding.ItemNoColorBinding
import com.neonkeyboard.cool.databinding.ItemStrokeBinding
import com.neonkeyboard.cool.ime.text.keyboard.TextKeyboardView
import com.neonkeyboard.cool.repository.PrefsReporitory
import com.neonkeyboard.cool.ui.base.AppBaseAdapter
import com.neonkeyboard.cool.ui.base.BaseViewModel
import com.neonkeyboard.cool.ui.base.createAdapter
import com.neonkeyboard.cool.ui.custom.HorizontalItemDecoration
import com.neonkeyboard.cool.util.SingleLiveData
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class ThemeEditorViewModel : BaseViewModel() {

    private val bgFolderNamePath = "images"

    val currentFont = ObservableField<Int?>()
    val currentKeyColor = ObservableField<String?>()
    val currentStrokeColor = ObservableField<String?>()
    val currentBackgroundColor = ObservableField<String?>()
    val currentButtonsColor = ObservableField<String?>()
    val currenetStrokeCornersRadius = ObservableField<Int?>()
    val currentKeyboardBackgorund = ObservableField<BackgroundAsset.BackgroundTheme?>()

    val colorPicker = SingleLiveData<ColorType>()

    val keyBGOpacity = ObservableField<Int>()
    val itemDecoration = HorizontalItemDecoration(40)
    val colorItemDecoration = HorizontalItemDecoration(55)
    val visibleLayoutId = ObservableField(R.id.layoutBackgrounds)
    val imagePicker = SingleLiveData<Unit>()
    val stokeBorderAdapter = createAdapter<StrokeType, ItemStrokeBinding>(R.layout.item_stroke) {
        initItems = getStrockes()
        onItemClick = { if (it.strokeRadius > -1) currenetStrokeCornersRadius.set(it.strokeRadius) }
    }

    val backgroundAdapter = createAdapter<BackgroundAsset, ViewDataBinding> {
        initItems = mutableListOf<BackgroundAsset>(BackgroundAsset.NewImage)
            .apply { addAll(readAssetImages()) }
        onItemClick = { handleBgClick(it) }
        viewBinding = { inflater, viewGroup, viewType -> getBackgroundBinding(viewType, inflater, viewGroup) }
        itemViewTypeProvider = { getBGItemType(it) }
    }

    private fun getBGItemType(it: BackgroundAsset) = when (it) {
        is BackgroundAsset.BackgroundTheme -> 1
        else -> 0
    }

    private fun getBackgroundBinding(viewType: Int, inflater: LayoutInflater, viewGroup: ViewGroup) = when (viewType) {
        0 -> ItemKeyboardBackgroundNewBinding.inflate(inflater, viewGroup, false)
        else -> ItemKeyboardBackgroundAssetBinding.inflate(inflater, viewGroup, false)
    }

    private fun handleBgClick(it: BackgroundAsset) {
        clearSelectedItemInAdapter(backgoroundColorAdapter)
        currentBackgroundColor.set(null)
        when (it) {
            is BackgroundAsset.NewImage -> imagePicker.postValue(Unit)
            is BackgroundAsset.BackgroundTheme -> currentKeyboardBackgorund.set(it)
        }
    }

    val fontsAdapter = createAdapter<KeyboardFont, ItemFontBinding>(R.layout.item_font) {
        onItemClick = {
            unselectOtherFonts(it)
            it.isSelected.set(true)
            currentFont.set(it.fontRes)
        }
    }

    private fun unselectOtherFonts(font: KeyboardFont) {
        fontsAdapter.getData().forEach {
            if (it !== font && it.isSelected.get()) {
                it.isSelected.set(false)
                return
            }
        }
    }

    val keyColorAdapter = createAdapter<ColorItem, ViewDataBinding> {
        initItems = getColors()
        viewBinding = { inflater, viewGroup, viewType -> getColorBinding(viewType, inflater, viewGroup) }
        itemViewTypeProvider = { getColorItemViewType(it) }
        onItemClick = { handleColorClick(it, ColorType.KEY) }
    }

    val backgoroundColorAdapter = createAdapter<ColorItem, ViewDataBinding> {
        initItems = getColors()
        viewBinding = { inflater, viewGroup, viewType -> getColorBinding(viewType, inflater, viewGroup) }
        itemViewTypeProvider = { getColorItemViewType(it) }
        onItemClick = { handleColorClick(it, ColorType.BACKGROUND) }
    }

    val buttonsColorAdapter = createAdapter<ColorItem, ViewDataBinding> {
        initItems = getColors()
        viewBinding = { inflater, viewGroup, viewType -> getColorBinding(viewType, inflater, viewGroup) }
        itemViewTypeProvider = { getColorItemViewType(it) }
        onItemClick = { handleColorClick(it, ColorType.BUTTONS) }
    }

    val strokeColorAdapter = createAdapter<ColorItem, ViewDataBinding> {
        initItems = getColors(NoColor)
        viewBinding = { inflater, viewGroup, viewType -> getColorBinding(viewType, inflater, viewGroup) }
        itemViewTypeProvider = { getColorItemViewType(it) }
        onItemClick = { handleColorClick(it, ColorType.STROKE) }
    }

    private fun handleColorClick(item: ColorItem, colorType: ColorType) {
        val currentAdapter = getAdapterByColorType(colorType)

        clearSelectedItemInAdapter(currentAdapter)

        when (item) {
            is Color -> {
                item.isSelected = true
                getColorObservableField(colorType).set(item.textColor)
                currentAdapter.updateItem(item)
                if (colorType == ColorType.BACKGROUND) currentKeyboardBackgorund.set(null)
            }
            is NewColor -> colorPicker.postValue(colorType)
            else -> currentStrokeColor.set(null)
        }
    }

    private fun getAdapterByColorType(colorType: ColorType) = when (colorType) {
        ColorType.KEY -> keyColorAdapter
        ColorType.BACKGROUND -> backgoroundColorAdapter
        ColorType.STROKE -> strokeColorAdapter
        ColorType.BUTTONS -> buttonsColorAdapter
    }

    private fun clearSelectedItemInAdapter(adapter: AppBaseAdapter<ColorItem, *>) {
        adapter.getData()
            .filterIsInstance(Color::class.java)
            .firstOrNull { it.isSelected }?.let {
                it.isSelected = false
                adapter.updateItem(it)
            }
    }

    private fun getColorItemViewType(item: ColorItem) = when (item) {
        is NewColor -> 0
        is NoColor -> 1
        else -> 2
    }

    private fun getColorBinding(viewType: Int, inflater: LayoutInflater, viewGroup: ViewGroup) = when (viewType) {
        0 -> ItemColorNewBinding.inflate(inflater, viewGroup, false)
        1 -> ItemNoColorBinding.inflate(inflater, viewGroup, false)
        else -> ItemColorBinding.inflate(inflater, viewGroup, false)
    }

    private fun readAssetImages(): List<BackgroundAsset> =
        NeonApplication.instance.assets
            .list(bgFolderNamePath)
            ?.filter { it.startsWith("image") }
            ?.map { BackgroundAsset.BackgroundTheme(uriPathFromAsset(it)) }
            ?: listOf()

    private fun uriPathFromAsset(it: String) =
        Uri.fromFile(File("//android_asset/${bgFolderNamePath}/$it")).toString()

    private fun getFontsList() = listOf(
        KeyboardFont("Arial", R.font.arial),
        KeyboardFont("Times New Roman", R.font.times),
        KeyboardFont("IBM Plex Mono", R.font.ibm_plex),
        KeyboardFont("Verdana", R.font.verdana),
        KeyboardFont("Roboto", R.font.roboto),
    )

    private fun getColors(noColorItem: NoColor? = null) = listOf(
        noColorItem,
        NewColor,
        Color("#000000", stockeColor = "#736D60").apply { isSelected = true },
        Color("#FFFEF9", isDarkBorder = true),
        Color("#C4C4C4"),
        Color("#626262"),
        Color("#FFE500"),
        Color("#FFB800"),
        Color("#FF8A00"),
        Color("#E20F02"),
        Color("#FF006B"),
        Color("#FF0099"),
        Color("#DB00FF"),
        Color("#8F00FF"),
        Color("#5200FF"),
        Color("#0500FF"),
        Color("#0085FF"),
        Color("#00C2FF"),
        Color("#00FFFF"),
        Color("#00FF94"),
        Color("#8FFF00"),
        Color("#FFEFCF", isDarkBorder = true),
        Color("#FFD98F", isDarkBorder = true)
    ).mapNotNull { it }

    private fun getStrockes() = listOf(
        StrokeType(R.drawable.stroke_1, 0),
        StrokeType(R.drawable.stroke_2, 4),
        StrokeType(R.drawable.stroke_3, 10),
        StrokeType(R.drawable.stroke_4, 15),
        StrokeType(R.drawable.stroke_5, 20),
    )

    fun onColorSelected(selectedColor: Int, colorType: ColorType) {
        clearSelectedItemInAdapter(getAdapterByColorType((colorType)))
        getColorObservableField(colorType).set(String.format("#%06X", 0xFFFFFF and selectedColor))
        if (colorType == ColorType.BACKGROUND) currentKeyboardBackgorund.set(null)
    }

    data class KeyboardFont(val name: String, val fontRes: Int) {
        val isSelected = ObservableBoolean(false)
    }

    sealed class BackgroundAsset {

        object NewImage : BackgroundAsset()

        data class BackgroundTheme(val uri: String, val backgroundTypeName: String? = null) : BackgroundAsset()
    }

    sealed class ColorItem

    data class Color(
        val textColor: String,
        val stockeColor: String? = null,
        private val isDarkBorder: Boolean = false
    ) : ColorItem() {

        var isSelected = false

        fun getBorderColor() = if (isDarkBorder) "#7D7D7D"
        else "#FFFFFF"
    }

    object NewColor : ColorItem()

    object NoColor : ColorItem()

    enum class ColorType {
        KEY,
        STROKE,
        BACKGROUND,
        BUTTONS
    }

    private fun getColorObservableField(colorType: ColorType) = when (colorType) {
        ColorType.STROKE -> currentStrokeColor
        ColorType.BACKGROUND -> currentBackgroundColor
        ColorType.KEY -> currentKeyColor
        ColorType.BUTTONS -> currentButtonsColor
    }

    fun setCustomBackground(imagePath: String?) {
        imagePath ?: return
        currentKeyboardBackgorund.set(BackgroundAsset.BackgroundTheme(imagePath))
    }

    data class StrokeType(val strokeRes: Int, val strokeRadius: Int)


    private fun removeThemeFile(keyboardId: Long) {
        File(NeonApplication.instance.filesDir, "${keyboardId}.png").deleteOnExit()
    }

    private fun saveKeyboardPreviewFile(bitmap: Bitmap, keyboardId: Long) {
        BufferedOutputStream(FileOutputStream(File(NeonApplication.instance.filesDir, "${keyboardId}.png")))
            .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    fun saveKeyboardImage(keyboardView: TextKeyboardView, keyboardId: Long?) {
        keyboardId ?: return
        removeThemeFile(keyboardId)
        saveKeyboardPreviewFile(keyboardView.drawToBitmap(Bitmap.Config.ARGB_8888), keyboardId)
    }

    fun setCurrentKeyboard(keyboardTheme: KeyboardTheme) {
        keyboardTheme.id = ThemeDataBase.dataBase.getThemesDao().insertTheme(keyboardTheme)
    }

    fun attachKeyboard(keyboardTheme: KeyboardTheme) {
        PrefsReporitory.keyboardTheme = keyboardTheme
    }

    fun initFontAdapter(keyFont: Int) {
        val fontList = getFontsList()
        fontList.firstOrNull { it.fontRes == keyFont }?.isSelected?.set(true)
        fontsAdapter.reloadData(fontList)
    }

    fun capture(keyboardPreview: TextKeyboardView, it: String) {
        val index = it.substring(0, it.indexOf("."))
        val bitmap = keyboardPreview.drawToBitmap(Bitmap.Config.ARGB_8888)
        val file = File(NeonApplication.instance.filesDir, "asset_$index.png")
        BufferedOutputStream(FileOutputStream(file))
            .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

        Log.d("12345", file.absolutePath)
    }
}
