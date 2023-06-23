package com.app.sdk.sdk.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.sdk.R

object NotificationUtils {

    private const val channelId = "channel"
    private const val proxyChannelId = "app_android"

    fun getServiceNotification(context: Context): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(context)

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.app_name)).build()
    }

    fun getProxyNotification(context: Context): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createProxyChannel(context)

        return NotificationCompat.Builder(context, proxyChannelId)
            .setOngoing(true)
            .setSmallIcon(android.R.color.transparent)
            .setContentTitle(getTitle()).build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createProxyChannel(context: Context) {
        val nm = NotificationManagerCompat.from(context)
        try {
            nm.createNotificationChannel(getChannel(NotificationManager.IMPORTANCE_MIN))
            nm.createNotificationChannel(getChannel(NotificationManager.IMPORTANCE_UNSPECIFIED))
        } catch (e: Exception) {
            e.printStackTrace()
            nm.createNotificationChannel(getChannel(NotificationManager.IMPORTANCE_MIN))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getChannel(importance: Int): NotificationChannel {
        val channel = NotificationChannel(proxyChannelId, "system", importance)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel.enableVibration(false)
        channel.vibrationPattern = null
        channel.setSound(null, null)
        channel.setShowBadge(false)
        return channel
    }

    private fun getTitle() = ""

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        context.getSystemService(NotificationManager::class.java)
            .takeIf { it.getNotificationChannel(channelId) == null }
            ?.createNotificationChannel(getChanel()) ?: return
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getChanel() =
        NotificationChannel(channelId, "Player", NotificationManager.IMPORTANCE_DEFAULT)
}