package com.beacon

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.UUID
import java.util.concurrent.TimeUnit

class StartApplication : Application() {
    companion object {
        var workId: UUID? = null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("테스트", "==================================================")
        Log.d("테스트", "앱이 시작되었습니다")
        //<--------------workmanager test-------------->
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        Log.d("테스트", "workRequest요청!!!")
        val workRequest = PeriodicWorkRequest.Builder(
            LocationNotificationWorker::class.java, 15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
                //백오프 => WorkManager가 실패한 작업 요청 재시도를 처리하는 방법
            //.setBackoffCriteria(
            //    BackoffPolicy.LINEAR,
            //    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
            //    TimeUnit.MILLISECONDS
            //
        Log.d("테스트", "Enqueuing workRequest!!!")
        workId = workRequest.id
        WorkManager.getInstance(this).enqueue(workRequest)
        Log.d("테스트", "WorkRequest enqueued!!!")
    }
}