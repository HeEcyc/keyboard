package dev.patrickgold.florisboard.ui.theme.editor.activity

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import dev.patrickgold.florisboard.FlorisApplication
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ItemKeyboardThemeBinding
import dev.patrickgold.florisboard.ui.base.BaseViewModel
import dev.patrickgold.florisboard.ui.base.createAdapter
import dev.patrickgold.florisboard.ui.custom.HorizontalItemDecoration
import dev.patrickgold.florisboard.ui.custom.ThemesItemDecoration
import java.io.File

class ThemeEditorViewModel : BaseViewModel() {
    private val bgFolderNamePath = "images"

    val itemDecoration = HorizontalItemDecoration(30)

    val backgroundAdapter =
        createAdapter<BackgroundAsset, ItemKeyboardThemeBinding>(R.layout.item_keyboard_background_asset) {
            initItems = readAssetImages()
        }

    private fun readAssetImages(): List<BackgroundAsset> =
        FlorisApplication.instance.assets
            .list(bgFolderNamePath)
            ?.map { BackgroundAsset.ImageAsset(Uri.fromFile(File("//android_asset/${bgFolderNamePath}/$it"))) }
            ?: listOf()

    sealed class BackgroundAsset {
        data class ImageAsset(val uri: Uri) : BackgroundAsset()
    }
}
