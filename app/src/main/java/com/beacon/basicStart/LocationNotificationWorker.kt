package com.beacon.basicStart

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.beacon.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class LocationNotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    companion object {
        private const val TAG = "테스트"
        private const val CHANNEL_ID = "location_notification_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun doWork(): Result {
        Log.d("테스트", "==================================================")
        Log.d(TAG, "doWork() started")
        return try {
            val location = fetchCurrentLocation()
            if (location != null) {
                Log.d(TAG, "doWork() 위치 : ${location}")
                //showLocationNotification(location)
                sendLocationToServer(location)
                Result.success()
            } else {
                Log.d(TAG, "doWork() 위치 : NULL")
                //showLocationNullNotification()
                Result.failure()
            }
        } catch (e: Exception) {
            Log.d(TAG, "doWork() 위치 : 에러 발생!")
            Result.failure()
        }
    }

    private fun sendLocationToServer(location: Location): Boolean {
        val url = "http://43.202.105.197:8080/api/v1/location-token"

        var fcm_tkn = ""
        val tokenTask = FirebaseMessaging.getInstance().token
        try {
            val token = Tasks.await(tokenTask)
            Log.d("테스트", "Token: $token")
            fcm_tkn = token
        } catch (e: Exception) {
            Log.d("테스트", "Failed to get token: ${e.message}")
        }

        val json = JSONObject().apply {
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("fcmToken", fcm_tkn)
        }

        val mediaType = MediaType.parse("application/json")
        val requestBody = RequestBody.create(mediaType, json.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Log.d("테스트", "요청: $json")
        Log.d("테스트", "요청: $request")
        val httpClient = OkHttpClient()
        val response = httpClient.newCall(request).execute()
        Log.d("테스트", "응답: $response")
        return response.isSuccessful
    }

    //<--------------현재 위치 함수---------------->
    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation(): Location? {
        Log.d(TAG, "fetchCurrentLocation() started")
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        return try {
            //권한 체크 후 위치 측정
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG,"fetchCurrentLocation() 권한 존재 O")
                val locationTask = fusedLocationClient.lastLocation
                //Log.d(TAG,"${locationTask}")
                val location = Tasks.await(locationTask)
                //Log.d(TAG,"${location}")
                location
            } else {
                Log.d(TAG,"fetchCurrentLocation() 권한 존재 X")
                // Handle the case when permission is not granted.
                // For demonstration purposes, I'm returning null.
                null
            }
        } catch (exception: Exception) {
            null
        }
    }

    private fun getLocationText(location: Location): String {
        Log.d(TAG, "getLocationText : 위치 체크")
        return "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
    }

    //<--------------위치 알림 발생---------------->
    private fun showLocationNotification(location: Location) {
        Log.d(TAG, "showLocationNotification() started")

        //권한 체크
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "showLocationNotification() Permission not granted for notification")
            return
        }

        // Retrieve the FCM
        var fcm_tkn = "비어있는 토큰"
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("테스트", "Token: $token")
                    fcm_tkn = token
                } else {
                    Log.d("테스트", "Failed to get token")
                }
            }

        //알림 생성
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_rain)
            .setContentTitle("현재 위치")//알람 제목
            .setContentText(getLocationText(location) + "\n" + fcm_tkn)//알람 내용 : 위치 정보
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            Log.d(TAG, "showLocationNotification() 알림 생성")
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    //null일 경우 오는 알람
    private fun showLocationNullNotification() {
        Log.d(TAG, "showLocationNullNotification() started")

        //권한 체크
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "showLocationNullNotification() Permission not granted for notification")
            return
        }

        //알림 생성
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_rain)
            .setContentTitle("현재 위치")//알람 제목
            .setContentText("현재 위치를 가져올 수 없습니다.")//알람 내용
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            Log.d(TAG, "showLocationNullNotification() 알림 생성")
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }


}
