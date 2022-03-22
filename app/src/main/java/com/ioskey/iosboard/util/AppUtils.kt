package com.ioskey.iosboard.util

import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.databinding.Observable
import com.google.gson.Gson
import com.ioskey.iosboard.NeonApplication
import com.ioskey.iosboard.data.KeyboardTheme
import com.ioskey.iosboard.repository.PrefsReporitory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File

fun Observable.addOnPropertyChangedCallback(
    callback: (Observable, Int) -> Unit
): Observable.OnPropertyChangedCallback =
    object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable, propertyId: Int) =
            callback(sender, propertyId)
    }.also { addOnPropertyChangedCallback(it) }

fun String.getFile(): File = File(NeonApplication.instance.filesDir, this)

fun String.fileExists(): Boolean = getFile().exists()

fun EditorInfo.isPasswordInputType(): Boolean {
    val variation = inputType and (InputType.TYPE_MASK_CLASS or InputType.TYPE_MASK_VARIATION)
    return (variation == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        || (variation == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD)
        || (variation == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
}

fun getPresetThemes(): List<KeyboardTheme> {
    val themeList: List<KeyboardTheme>
    runBlocking(Dispatchers.IO) {
        val gson = Gson()
        val assetFolder = "ime/theme"
        val assets = NeonApplication.instance.assets

        themeList = assets.list(assetFolder)!!
            .map { assets.open("${assetFolder}/$it").bufferedReader().use { theme -> theme.readText() } }
            .map { gson.fromJson(it, KeyboardTheme::class.java) }
            .sortedBy { it.index }

        if (PrefsReporitory.keyboardTheme?.id == null) themeList.forEach {
            it.isSelected = it.backgroundImagePath == PrefsReporitory.keyboardTheme?.backgroundImagePath
        }
    }
    return themeList
}
