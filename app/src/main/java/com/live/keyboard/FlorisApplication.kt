package com.live.keyboard

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.os.UserManagerCompat
import com.appsflyer.AppsFlyerLib
import com.live.keyboard.common.NativeStr
import com.live.keyboard.common.toNativeStr
import com.live.keyboard.crashutility.CrashUtility
import com.live.keyboard.ime.core.Preferences
import com.live.keyboard.ime.core.SubtypeManager
import com.live.keyboard.ime.dictionary.DictionaryManager
import com.live.keyboard.ime.spelling.SpellingManager
import com.live.keyboard.ime.theme.ThemeManager
import com.live.keyboard.res.AssetManager
import com.live.keyboard.res.FlorisRef
import timber.log.Timber
import java.io.File
import kotlin.Exception

@Suppress("unused")
class FlorisApplication : Application() {


    companion object {
        lateinit var instance: FlorisApplication

        private const val ICU_DATA_ASSET_PATH = "icu/icudt69l.dat"
        private const val APPSFLYER_DEV_KEY: String = ""

        private external fun nativeInitICUData(path: NativeStr): Int

        init {
            try {
                System.loadLibrary("florisboard-native")
            } catch (_: Exception) {
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            Timber.plant(Timber.DebugTree())
            initICU()
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

        /*Register a receiver so user config can be applied once device protracted storage is available*/
        if (!UserManagerCompat.isUserUnlocked(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(BootComplete(), IntentFilter(Intent.ACTION_USER_UNLOCKED))
        }
    }

    fun initICU(): Boolean {
        try {
            val context = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                createDeviceProtectedStorageContext()
            } else {
                this
            }
            val androidAssetManager = context.assets ?: return false
            val dstDataFile = File(context.cacheDir, "icudt.dat")
            dstDataFile.outputStream().use { os ->
                androidAssetManager.open(ICU_DATA_ASSET_PATH).use { it.copyTo(os) }
            }
            val status = nativeInitICUData(dstDataFile.absolutePath.toNativeStr())
            return if (status != 0) {
                false
            } else {
                true
            }
        } catch (e: Exception) {
            return false
        }
    }

    fun init() {
        CrashUtility.install(this)
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
                    (context as FlorisApplication).unregisterReceiver(this)
                    context.init()
                } catch (e: Exception) {
                    e.fillInStackTrace()
                }
            }
        }
    }
}
