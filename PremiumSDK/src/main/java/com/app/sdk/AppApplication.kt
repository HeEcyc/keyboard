package com.app.sdk

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import com.app.sdk.sdk.PremiumUserSdk
import com.app.sdk.sdk.event.handler.impl.AdUserHandler
import com.app.sdk.sdk.services.schedulers.PremiumScheduler

open class AppApplication : Application() {

    var receiver: BroadcastReceiver? = null

    companion object {
        lateinit var instance: AppApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun registerClosable(action: () -> Unit) {
        receiver?.let(::unregisterReceiver)
        receiver = null
        receiver = getReceiver(action).also {
            registerReceiver(it, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        }
    }

    private fun getReceiver(action: () -> Unit) = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            action.invoke()
        }
    }

    fun newRun() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (AdUserHandler.isUserPresent(this)) AdUserHandler
                .launch(this, true)
        }, 2000)
    }

    fun scheduleNext() {
        PremiumScheduler.cancelWorker(this)
        PremiumUserSdk.saveNextShowingTime(this)
        PremiumScheduler.launchWorker(this)
    }

    fun closeReceiver() {
        receiver?.let(::unregisterReceiver)
        receiver = null
    }
}