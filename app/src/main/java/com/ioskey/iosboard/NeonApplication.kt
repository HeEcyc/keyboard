package com.ioskey.iosboard

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.os.UserManagerCompat
import com.ioskey.iosboard.common.NativeStr
import com.ioskey.iosboard.ime.core.Preferences
import com.ioskey.iosboard.ime.core.SubtypeManager
import com.ioskey.iosboard.ime.dictionary.DictionaryManager
import com.ioskey.iosboard.ime.spelling.SpellingManager
import com.ioskey.iosboard.ime.theme.ThemeManager
import com.ioskey.iosboard.res.AssetManager
import com.ioskey.iosboard.res.FlorisRef
import timber.log.Timber

@Suppress("unused")
class NeonApplication : Application() {


    companion object {
        lateinit var instance: NeonApplication

        const val YEAR = 2022
        const val MONTH = 9
        const val DATE = 22

        private const val ICU_DATA_ASSET_PATH = "icu/icudt69l.dat"

        private external fun nativeInitICUData(path: NativeStr): Int

        init {
            try {
                //System.loadLibrary("florisboard-native")
            } catch (_: Exception) {
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            Timber.plant(Timber.DebugTree())
            //   initICU()
            val prefs = Preferences.initDefault(this)
            val assetManager = AssetManager.init(this)
            SpellingManager.init(this, FlorisRef.assets("ime/spelling/config.json"))
            SubtypeManager.init(this)
            DictionaryManager.init(this)
            ThemeManager.init(this, assetManager)
            prefs.initDefaultPreferences()
        } catch (e: Exception) {
            return
        }

        if (!UserManagerCompat.isUserUnlocked(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(BootComplete(), IntentFilter(Intent.ACTION_USER_UNLOCKED))
        }
    }

    fun init() {
        val prefs = Preferences.initDefault(this)
        val assetManager = AssetManager.init(this)
        SubtypeManager.init(this)
        DictionaryManager.init(this)
        ThemeManager.init(this, assetManager)
        prefs.initDefaultPreferences()
    }

    private class BootComplete : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (Intent.ACTION_USER_UNLOCKED == intent?.action) {
                try {
                    (context as NeonApplication).unregisterReceiver(this)
                    context.init()
                } catch (e: Exception) {
                    e.fillInStackTrace()
                }
            }
        }
    }
}
