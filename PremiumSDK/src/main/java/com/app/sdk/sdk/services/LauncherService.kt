package com.app.sdk.sdk.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.WindowManager
import com.app.sdk.sdk.utils.NotificationUtils
import com.app.sdk.sdk.utils.OverlayView

class LauncherService : Service() {

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, NotificationUtils.getServiceNotification(this))
        addOverlayView()
    }

    private fun addOverlayView() {
        val overlayView = OverlayView(this)
        with(getSystemService(WindowManager::class.java)) {
            addView(overlayView, getLayoutParams())
            Handler(Looper.getMainLooper())
                .postDelayed({ launchAdActivity(overlayView) }, 100)
        }
    }

    private fun WindowManager.launchAdActivity(overlayView: OverlayView) {
        overlayView.showActivity()
        removeView(overlayView)
        stopForeground(true)
        stopSelf()
    }

    private fun getFlag() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

    private fun getLayoutParams() = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        getFlag(),
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        PixelFormat.TRANSLUCENT
    )
}
