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
import androidx.work.ExistingPeriodicWorkPolicy
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
        val workManager = WorkManager.getInstance(this) // WorkManager 인스턴스를 가져옵니다.
        val uniqueWorkName = "my_unique_recurring_work_name" // 작업의 고유 이름을 정의합니다.

        // 이미 큐에 동일한 이름의 작업이 있는지 확인합니다.
        val workQuery = workManager.getWorkInfosForUniqueWork(uniqueWorkName)
        val workInfoList = workQuery.get()

        if (workInfoList.isEmpty()) { // 작업이 큐에 없는 경우
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            Log.d("테스트", "workRequest 요청!!!")
            val workRequest = PeriodicWorkRequest.Builder(
                LocationNotificationWorker::class.java, 15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            Log.d("테스트", "Enqueuing workRequest!!!")
            workId = workRequest.id // 작업 ID를 저장합니다.
            // 작업을 큐에 추가합니다. 기존 작업이 없으므로 새 작업이 추가됩니다.
            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.KEEP, // 기존 작업 유지 정책을 사용합니다.
                workRequest
            )
            Log.d("테스트", "WorkRequest enqueued!!!")
        } else {
            Log.d("테스트", "WorkRequest already exists!!!") // 작업이 이미 큐에 있는 경우
            val workManager = WorkManager.getInstance(this)
            workManager.cancelAllWork()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            Log.d("테스트", "workRequest 요청!!!")
            val workRequest = PeriodicWorkRequest.Builder(
                LocationNotificationWorker::class.java, 15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            Log.d("테스트", "Enqueuing workRequest!!!")
            workId = workRequest.id // 작업 ID를 저장합니다.
            // 작업을 큐에 추가합니다. 기존 작업이 없으므로 새 작업이 추가됩니다.
            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.KEEP, // 기존 작업 유지 정책을 사용합니다.
                workRequest
            )
            Log.d("테스트", "WorkRequest enqueued!!!")
        }
    }
}
