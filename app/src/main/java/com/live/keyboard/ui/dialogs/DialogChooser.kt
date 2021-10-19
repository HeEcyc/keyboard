package com.live.keyboard.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.IBinder
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
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

    fun show(context: Context, token: IBinder) {
        AlertDialog.Builder(context, R.style.DefaultAlertDialogTheme).apply {
            setTitle(getBlackTitle(context.getString(R.string.language_change), context))
            setSingleChoiceItems(
                items.map { toStringWithContext(it, context) }.toTypedArray(),
                items.indexOf(selectedItem)
            ) { dialog, which ->
                onItemSelected.invoke(items[which])
                dialog.dismiss()
            }
            setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
        }.create().apply {
            val textViewId: Int = getContext().resources.getIdentifier("android:id/alertTitle", null, null)
            findViewById<TextView>(textViewId)
                ?.setTextColor(ContextCompat.getColor(context, R.color.textColorMain))
            val lp = window?.attributes
            lp?.token = token
            lp?.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG
            window?.attributes = lp
            window?.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            setOnShowListener {
                getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context, R.color.blueColor))
            }
        }.show()
    }

    private fun getBlackTitle(title: String, context: Context) = SpannableStringBuilder(title).apply {
        setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.textColorMain)
            ), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}
