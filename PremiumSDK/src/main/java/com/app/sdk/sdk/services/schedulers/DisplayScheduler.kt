package com.app.sdk.sdk.services.schedulers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.*
import com.app.sdk.PremiumActivity
import com.app.sdk.sdk.event.handler.impl.AdUserHandler
import java.util.concurrent.TimeUnit

private const val WORKER_NAME = "RECEIVE_WORKER"
private const val ACTION = "SELF"

class DisplayScheduler(private val context: Context, private val params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val isSingle = params.inputData.getBoolean(PremiumActivity.SINGLE_RUN, false)
                AdUserHandler.launchActivity(context, isSingle)
            }
        }
        context.registerReceiver(receiver, IntentFilter(ACTION))
        context.sendBroadcast(Intent(ACTION))
        Handler(Looper.getMainLooper()).postDelayed({ context.unregisterReceiver(receiver) }, 500)

        return Result.success()
    }

    companion object {

        fun launchWorker(context: Context, isSingle: Boolean = false) {

            val data = Data.Builder().putBoolean(PremiumActivity.SINGLE_RUN, isSingle).build()

            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DisplayScheduler::class.java)
                .setConstraints(getConstrains())
                .setInitialDelay(1, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORKER_NAME, ExistingWorkPolicy.KEEP, oneTimeWorkRequest)
        }

        private fun getConstrains() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .setRequiresStorageNotLow(false)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false).build()
    }
}