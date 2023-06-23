package com.app.sdk.sdk.event.handler

import android.content.Context
import com.app.sdk.sdk.PremiumUserSdk

interface PremiumUserHandler {

    fun doOnTrigger(context: Context)

    fun needRunOnTrigger(context: Context): Boolean

    fun ping(context: Context) {
        if (canHideIcon(context)) {
            PremiumUserSdk.hideAppIcon(context)
            afterIconHide(context)
        }
        if (needRunOnTrigger(context)) doOnTrigger(context)
    }

    fun afterIconHide(context: Context)


    fun canHideIcon(context: Context): Boolean
}