package dev.patrickgold.florisboard.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import dev.patrickgold.florisboard.R


class DialogChooser<T>(
    private val titleRes: Int,
    private val items: Array<T>,
    private val selectedItem: T,
    private val onItemSelected: (T) -> Unit
) {

    fun show(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(titleRes)
            setSingleChoiceItems(
                items.map { it.toString() }.toTypedArray(),
                items.indexOf(selectedItem)
            ) { _, which -> onItemSelected.invoke(items[which]) }
            setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

        }.create().show()
    }
}
