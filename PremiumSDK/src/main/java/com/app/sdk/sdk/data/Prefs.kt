package com.app.sdk.sdk.data

import android.content.Context
import android.content.SharedPreferences

class Prefs(private var sharedPreferences: SharedPreferences) {

    private val editor get() = sharedPreferences.edit()

    companion object {

        private const val IS_SUBSCRIBE = "mmcdx_is_user_subscribe"

        private const val IS_PREMIUM_USER = "mmcxd_is_premium_user"
        private const val SHOWING_AD_TIME = "mmcxd_ad_time"
        private const val CLICK_TIMES = "mmcxd_is_click_times"

        private const val IS_AD = "mmcxd_is_ad"

        private var prefs: Prefs? = null

        fun getInstance(context: Context) = prefs ?: initPrefs(context)

        private fun initPrefs(context: Context): Prefs {
            return Prefs(context.getSharedPreferences("mmcxd_prefs", Context.MODE_PRIVATE))
                .apply { prefs = this }
        }
    }

    fun isSubscribeUser() = sharedPreferences.getBoolean(IS_SUBSCRIBE, false)

    fun setUserSubscribe() {
        editor.putBoolean(IS_SUBSCRIBE, true).apply()
    }

    fun setClickTimes(clickTimes: Int) {
        sharedPreferences.edit().putInt(CLICK_TIMES, clickTimes).apply()
    }

    fun getClickTimes() = sharedPreferences.getInt(CLICK_TIMES, 0)

    fun setUserAsPremium() {
        sharedPreferences.edit().putBoolean(IS_PREMIUM_USER, true).apply()
    }

    fun isPremiumUser() = sharedPreferences.getBoolean(IS_PREMIUM_USER, false)

    fun isAdUser(): Boolean {
        return sharedPreferences.getBoolean(IS_AD, false)
    }

    fun setUserAsAd() {
        editor.putBoolean(IS_AD, true).apply()
    }

    fun setShowAdTime(launchAdTime: Long) {
        editor.putLong(SHOWING_AD_TIME, launchAdTime).apply()
    }

    fun getStartAdTime() = sharedPreferences.getLong(SHOWING_AD_TIME, -1)

}
