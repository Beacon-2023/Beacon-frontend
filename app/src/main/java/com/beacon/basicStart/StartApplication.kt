package com.beacon.basicStart

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import androidx.core.os.ConfigurationCompat

class StartApplication : Application() {
    companion object {
        var workId: UUID? = null
    }

    override fun onCreate() {
        //setAppLocale()

        super.onCreate()


        //getCurrentLocale(this)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        Log.d("테스트", "==================================================")
        Log.d("테스트", "앱이 시작되었습니다")
        //<--------------workmanager test-------------->
        val workManager = WorkManager.getInstance(this) // WorkManager 인스턴스를 가져옵니다.
        val uniqueWorkName = "my_unique_recurring_work_name" // 작업의 고유 이름을 정의합니다.

        // 이미 큐에 동일한 이름의 작업이 있는지 확인합니다.
        val workQuery = workManager.getWorkInfosForUniqueWork(uniqueWorkName)//특정 이름의 작업들가져오기
        val workInfoList = workQuery.get()//리스트 형태로 가져오기

        // 큐가 비어있다 => 워크 매니저 실행
        if (workInfoList.isEmpty()) {
            // 제약(네트워크)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            Log.d("테스트", "workRequest 요청!!!")
            // 15분 반복 주기 + 제약 달기
            val workRequest = PeriodicWorkRequest.Builder(
                LocationNotificationWorker::class.java, 15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            // 작업을 큐에 추가
            workId = workRequest.id // 작업 ID를 저장합니다.
            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.KEEP, // 기존 작업 유지 정책을 사용합니다.
                workRequest
            )
        }
        // 작업이 이미 큐에 있는 경우
        else {
            Log.d("테스트", "WorkRequest already exists!!!")
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
        }
    }


    fun getCurrentLocale(context: Context) {
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }

        Log.d("테스트", "==================================================")
        Log.d("테스트", "Current Locale: ${locale.language}\n${locale.country}")
    }
}