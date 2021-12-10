package com.neonkeyboard.cool.util.hiding

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object HideAppUtil {

    fun hideApp(context: Context, newAlias: String, currentAlias: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !Build.MANUFACTURER.equals("xiaomi", true)
        ) context.packageManager.setComponentEnabledSetting(
            ComponentName(context, context.packageName.plus(".$newAlias")),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, context.packageName.plus(".$currentAlias")),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

}
