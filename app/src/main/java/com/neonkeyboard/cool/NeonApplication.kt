package com.neonkeyboard.cool

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.os.UserManagerCompat
import com.appsflyer.AppsFlyerLib
import com.neonkeyboard.cool.common.NativeStr
import com.neonkeyboard.cool.ime.core.Preferences
import com.neonkeyboard.cool.ime.core.SubtypeManager
import com.neonkeyboard.cool.ime.dictionary.DictionaryManager
import com.neonkeyboard.cool.ime.spelling.SpellingManager
import com.neonkeyboard.cool.ime.theme.ThemeManager
import com.neonkeyboard.cool.res.AssetManager
import com.neonkeyboard.cool.res.FlorisRef
import timber.log.Timber

@Suppress("unused")
class NeonApplication : Application() {


    companion object {
        lateinit var instance: NeonApplication

        private const val ICU_DATA_ASSET_PATH = "icu/icudt69l.dat"
        private const val APPSFLYER_DEV_KEY: String = ""

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
            AppsFlyerLib.getInstance().init(APPSFLYER_DEV_KEY, null, this)
            AppsFlyerLib.getInstance().start(this)
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
