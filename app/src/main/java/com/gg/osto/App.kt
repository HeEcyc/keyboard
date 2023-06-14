package com.gg.osto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.os.UserManagerCompat
import com.app.sdk.AppApplication
import com.gg.osto.common.NativeStr
import com.gg.osto.ime.core.Preferences
import com.gg.osto.ime.core.SubtypeManager
import com.gg.osto.ime.dictionary.DictionaryManager
import com.gg.osto.ime.spelling.SpellingManager
import com.gg.osto.ime.theme.ThemeManager
import com.gg.osto.res.AssetManager
import com.gg.osto.res.FlorisRef
import timber.log.Timber

@Suppress("unused")
class App : AppApplication() {


    companion object {
        lateinit var instance: App

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
                    (context as App).unregisterReceiver(this)
                    context.init()
                } catch (e: Exception) {
                    e.fillInStackTrace()
                }
            }
        }
    }
}
