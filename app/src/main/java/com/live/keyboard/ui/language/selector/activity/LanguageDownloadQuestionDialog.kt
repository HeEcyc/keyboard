package com.live.keyboard.ui.language.selector.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.live.keyboard.R
import com.live.keyboard.util.enums.Language

class LanguageDownloadQuestionDialog(
    context: Context,
    language: Language,
    onPositive: (DialogInterface) -> Unit = {},
    onNegative: (DialogInterface) -> Unit = {}
) {

    private val dialog = AlertDialog
        .Builder(context)
        .setTitle(language.languageName)
        .setMessage(context.getString(R.string.this_language_weighs_x_mb_do_you_want_to_download, language.weightMB))
        .setPositiveButton(R.string.language_download_question_dialog_positive) { dialog, _ -> onPositive(dialog); dialog.dismiss() }
        .setNegativeButton(R.string.language_download_question_dialog_negative) { dialog, _ -> onNegative(dialog); dialog.dismiss() }
        .create()

    fun show() = dialog.show()

}
