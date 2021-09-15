package com.live.keyboard.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import com.live.keyboard.R


class DialogChooser<T>(
    private val titleRes: Int,
    private val items: Array<T>,
    private val selectedItem: T,
    private val toStringWithContext: (T, Context) -> String = { item, _ -> item.toString() },
    private val onItemSelected: (T) -> Unit
) {

    fun show(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(titleRes)
            setSingleChoiceItems(
                items.map { toStringWithContext(it, context) }.toTypedArray(),
                items.indexOf(selectedItem)
            ) { dialog, which ->
                onItemSelected.invoke(items[which])
                dialog.dismiss()
            }
            setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

        }.create().show()
    }
}
