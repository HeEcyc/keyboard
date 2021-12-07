package com.neonkeyboard.cool.util

import android.content.Context
import android.provider.Settings
import com.neonkeyboard.cool.BuildConfig

private const val IME_ID: String = "com.live.keyboard/.background.view.keyboard.BackgroundViewKeyboardService"
private const val IME_ID_BETA: String =
    "com.live.keyboard.beta/com.live.keyboard.background.view.keyboard.BackgroundViewKeyboardService"
private const val IME_ID_DEBUG: String =
    "com.live.keyboard.debug/com.live.keyboard.background.view.keyboard.BackgroundViewKeyboardService"

fun checkIfImeIsEnabled(context: Context): Boolean {
    val activeImeIds = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_INPUT_METHODS
    ) ?: "(none)"
    return when {
        BuildConfig.DEBUG -> {
            activeImeIds.split(":").contains(IME_ID_DEBUG)
        }
        context.packageName.endsWith(".beta") -> {
            activeImeIds.split(":").contains(IME_ID_BETA)
        }
        else -> {
            activeImeIds.split(":").contains(IME_ID)
        }
    }
}

fun checkIfImeIsSelected(context: Context): Boolean {
    val selectedImeId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.DEFAULT_INPUT_METHOD
    ) ?: "(none)"
    return when {
        BuildConfig.DEBUG -> {
            selectedImeId == IME_ID_DEBUG
        }
        context.packageName.endsWith(".beta") -> {
            selectedImeId.split(":").contains(IME_ID_BETA)
        }
        else -> {
            selectedImeId == IME_ID
        }
    }
}
