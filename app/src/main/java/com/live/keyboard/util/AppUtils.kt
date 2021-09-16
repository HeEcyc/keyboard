package com.live.keyboard.util

import androidx.databinding.Observable
import com.live.keyboard.FlorisApplication
import java.io.File

fun Observable.addOnPropertyChangedCallback(
    callback: (Observable, Int) -> Unit
): Observable.OnPropertyChangedCallback =
    object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable, propertyId: Int) =
            callback(sender, propertyId)
    }.also { addOnPropertyChangedCallback(it) }

fun String.getFile(): File = File(FlorisApplication.instance.filesDir, this)

fun String.fileExists(): Boolean = getFile().exists()
