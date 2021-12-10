package com.neonkeyboard.cool.util.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.ui.splash.activity.SplashActivity
import java.util.concurrent.TimeUnit

class AlarmBroadcast : BroadcastReceiver() {

    companion object {

        fun startAlarm(context: Context) {

            val am = context.getSystemService(AlarmManager::class.java)

            am.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2),
                getPendingInten(context)
            )
        }


        @SuppressLint("UnspecifiedImmutableFlag")
        private fun getPendingInten(context: Context): PendingIntent {
            val intent = Intent(context, AlarmBroadcast::class.java)
            intent.action = "notify"
            return PendingIntent.getBroadcast(
                context,
                10,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "notify" && !Settings.canDrawOverlays(context))
            showNotification(context)
        startAlarm(context)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(context: Context) {
        val channel = context.getString(R.string.app_name)

        val nm = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (nm.getNotificationChannel(channel) == null)
                NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_HIGH)
                    .let(nm::createNotificationChannel)
        }

        val pi = PendingIntent
            .getActivity(
                context,
                0,
                Intent(context, SplashActivity::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT
            )

        NotificationCompat.Builder(context, channel)
            .setContentText("Don't forget to add necessary permission to use extra\nfunctions of application")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentIntent(pi)
            .build()
            .let { nm.notify(0, it) }
    }


}
