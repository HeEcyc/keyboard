package com.app.sdk.sdk.services

import com.app.sdk.sdk.PremiumUserSdk
import com.app.sdk.sdk.data.Prefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        PremiumUserSdk.onPing(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (Prefs.getInstance(this).isPremiumUser()) PremiumUserSdk.subscribeUser()
    }
}
