package dev.patrickgold.florisboard.ui.theme.editor.activity

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import dev.patrickgold.florisboard.FlorisApplication
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ItemColorBinding
import dev.patrickgold.florisboard.databinding.ItemColorNewBinding
import dev.patrickgold.florisboard.databinding.ItemFontBinding
import dev.patrickgold.florisboard.databinding.ItemKeyboardThemeBinding
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

    val keyColorAdapter = createAdapter<TextColorItem, ViewDataBinding> {
        initItems = getColors()
        viewBinding = { inflater, viewGroup, viewType ->
            when (viewType) {
                0 -> ItemColorNewBinding.inflate(inflater, viewGroup, false)
                else -> ItemColorBinding.inflate(inflater, viewGroup, false)
            }
        }
        itemViewTypeProvider = { if (it is NewColor) 0 else 1 }
    }

    val backgoroundColorAdapter = createAdapter<TextColorItem, ViewDataBinding> {
        initItems = getColors()
        viewBinding = { inflater, viewGroup, viewType ->
            when (viewType) {
                0 -> ItemColorNewBinding.inflate(inflater, viewGroup, false)
                else -> ItemColorBinding.inflate(inflater, viewGroup, false)
            }
        }
        itemViewTypeProvider = { if (it is NewColor) 0 else 1 }
    }

    private fun getColors() = listOf(
        NewColor,
        TextColor("#000000", stockeColor = "#736D60")
            .apply { isSelected = true },
        TextColor("#FFFEF9", isDarkBorder = true),
        TextColor("#C4C4C4"),
        TextColor("#626262"),
        TextColor("#FFE500"),
        TextColor("#FFB800"),
        TextColor("#FF8A00"),
        TextColor("#E20F02"),
        TextColor("#FF006B"),
        TextColor("#FF0099"),
        TextColor("#DB00FF"),
        TextColor("#8F00FF"),
        TextColor("#5200FF"),
        TextColor("#0500FF"),
        TextColor("#0085FF"),
        TextColor("#00C2FF"),
        TextColor("#00FFFF"),
        TextColor("#00FF94"),
        TextColor("#8FFF00"),
        TextColor("#FFEFCF", isDarkBorder = true),
        TextColor("#FFD98F", isDarkBorder = true)
    )

    val backgroundAdapter =
        createAdapter<BackgroundAsset, ItemKeyboardThemeBinding>(R.layout.item_keyboard_background_asset) {
            initItems = readAssetImages()
            onItemClick = { if (it is BackgroundAsset.ImageAsset) testImage.set(it.uri) }
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

    data class KeyboardFont(val name: String, val fontRes: Int, var isSelected: Boolean = false)

    sealed class BackgroundAsset {
        data class ImageAsset(val uri: Uri) : BackgroundAsset()
    }

    sealed class TextColorItem

    data class TextColor(
        val textColor: String,
        val stockeColor: String? = null,
        private val isDarkBorder: Boolean = false
    ) : TextColorItem() {

        var isSelected = false

        fun getBorderColor() = if (isDarkBorder) "#7D7D7D"
        else "#FFFFFF"
    }

    object NewColor : TextColorItem()
}
