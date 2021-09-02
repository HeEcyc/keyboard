package dev.patrickgold.florisboard.ui.theme.editor.activity

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import dev.patrickgold.florisboard.FlorisApplication
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ItemColorBinding
import dev.patrickgold.florisboard.databinding.ItemColorNewBinding
import dev.patrickgold.florisboard.databinding.ItemFontBinding
import dev.patrickgold.florisboard.databinding.ItemKeyboardThemeBinding
import dev.patrickgold.florisboard.databinding.ItemNoColorBinding
import dev.patrickgold.florisboard.databinding.ItemStrokeBinding
import dev.patrickgold.florisboard.ui.base.BaseViewModel
import dev.patrickgold.florisboard.ui.base.createAdapter
import dev.patrickgold.florisboard.ui.custom.HorizontalItemDecoration
import java.io.File

class ThemeEditorViewModel : BaseViewModel() {

    private val bgFolderNamePath = "images"
    val currentFont = ObservableField<Int>()

    val itemDecoration = HorizontalItemDecoration(40)
    val colorItemDecoration = HorizontalItemDecoration(50)

    val testImage = ObservableField<Uri>()
    val visibleLayoutId = ObservableField(R.id.layoutBackgrounds)

    val fontsAdapter = createAdapter<KeyboardFont, ItemFontBinding>(R.layout.item_font) {
        initItems = getFontsList()
        onItemClick = { currentFont.set(it.fontRes) }
    }

    val keyColorAdapter = createAdapter<ColorItem, ViewDataBinding> {
        initItems = getColors()
        viewBinding = { inflater, viewGroup, viewType -> getColorBinding(viewType, inflater, viewGroup) }
        itemViewTypeProvider = { getColorItemViewType(it) }
    }

    val backgoroundColorAdapter = createAdapter<ColorItem, ViewDataBinding> {
        initItems = getColors()
        viewBinding = { inflater, viewGroup, viewType -> getColorBinding(viewType, inflater, viewGroup) }
        itemViewTypeProvider = { getColorItemViewType(it) }
    }

    val stokeColorAdapter = createAdapter<ColorItem, ViewDataBinding> {
        initItems = getColors(NoColor)
        viewBinding = { inflater, viewGroup, viewType -> getColorBinding(viewType, inflater, viewGroup) }
        itemViewTypeProvider = { getColorItemViewType(it) }
    }

    val stokeBorderAdapter = createAdapter<Int, ItemStrokeBinding>(R.layout.item_stroke) {
        initItems = getStrockes()
    }

    val backgroundAdapter =
        createAdapter<BackgroundAsset, ItemKeyboardThemeBinding>(R.layout.item_keyboard_background_asset) {
            initItems = readAssetImages()
            onItemClick = { if (it is BackgroundAsset.ImageAsset) testImage.set(it.uri) }
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
        FlorisApplication.instance.assets
            .list(bgFolderNamePath)
            ?.map { BackgroundAsset.ImageAsset(Uri.fromFile(File("//android_asset/${bgFolderNamePath}/$it"))) }
            ?: listOf()

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
        Color("#000000", stockeColor = "#736D60")
            .apply { isSelected = true },
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
        R.drawable.ic_no_border,
        R.drawable.stroke_1,
        R.drawable.stroke_2,
        R.drawable.stroke_3,
        R.drawable.stroke_4,
        R.drawable.stroke_5
    )

    data class KeyboardFont(val name: String, val fontRes: Int, var isSelected: Boolean = false)

    sealed class BackgroundAsset {
        data class ImageAsset(val uri: Uri) : BackgroundAsset()
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
}
