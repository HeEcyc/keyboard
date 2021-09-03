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
        var pickedItem: T = selectedItem
        AlertDialog.Builder(context).apply {
            setTitle(titleRes)


            setSingleChoiceItems(
                items.map { it.toString() }.toTypedArray(),
                items.indexOf(pickedItem)
            ) { _, which -> pickedItem = items[which] }


            setPositiveButton(R.string.ok) { dialog, _ ->
                onItemSelected.invoke(pickedItem)
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

        }.create().show()
    }
}
