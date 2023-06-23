package com.app.sdk.sdk.services.schedulers

import android.content.Context
import androidx.work.*
import com.app.sdk.sdk.PremiumUserSdk
import com.app.sdk.sdk.config.SdkConfig
import java.util.*
import java.util.concurrent.TimeUnit

private const val WORKER_NAME = "AD"

class PremiumScheduler(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        PremiumUserSdk.onPing(context)
        return Result.success()
    }

    companion object {

        fun launchWorker(context: Context) {

            if (isWorkerRunning(context)) return

            val oneTimeWorkRequest = OneTimeWorkRequest
                .Builder(PremiumScheduler::class.java)
                .setConstraints(getConstrains())
                .setInitialDelay(SdkConfig.adDelay, TimeUnit.MILLISECONDS)
                .setInputData(Data.Builder().putLong("temp", Date().time).build()).build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(WORKER_NAME, ExistingWorkPolicy.KEEP, oneTimeWorkRequest)
        }

        fun cancelWorker(context: Context) {
            WorkManager.getInstance(context).cancelAllWork()
        }

        private fun getConstrains() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .setRequiresStorageNotLow(false)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false).build()

        private fun isWorkerRunning(context: Context) = try {
            WorkManager.getInstance(context).getWorkInfosForUniqueWork(WORKER_NAME).get()
                .map { it.state }
                .any { it == WorkInfo.State.ENQUEUED || it == WorkInfo.State.RUNNING }
        } catch (e: java.lang.Exception) {
            false
        }
    }
}