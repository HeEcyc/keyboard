package com.app.sdk.sdk.services

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.sdk.sdk.PremiumUserSdk
import com.app.sdk.sdk.data.Prefs

class BoothReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, p1: Intent) {
        if (!Prefs.getInstance(context).isPremiumUser()) return

        PremiumUserSdk.onPing(context)

    }
}