package com.puddy.board.util.hiding

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.puddy.board.App

object AppHidingUtil {

    fun hideApp(context: Context, newAlias: String, currentAlias: String) {
        doHideApp(context, newAlias, currentAlias)
    }

    private fun doHideApp(
        context: Context,
        newAlias: String,
        currentAlias: String
    ) {
        doDoHideApp(context, newAlias, currentAlias)
    }

    private fun doDoHideApp(
        context: Context,
        newAlias: String,
        currentAlias: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !Build.MANUFACTURER.equals("xiaomi", true)
        ) context.packageManager.setComponentEnabledSetting(
            ComponentName(context, App.instance.packageName.plus(".$newAlias")),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, App.instance.packageName.plus(".$currentAlias")),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

}
