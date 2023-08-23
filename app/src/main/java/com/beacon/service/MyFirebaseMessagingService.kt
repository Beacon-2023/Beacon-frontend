package com.beacon.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.beacon.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "테스트"
        val notificationId = 123 // Use a value that makes sense for your app
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "==================================================")
        Log.d(TAG, "FCM 수신 확인")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Handle both data payload and notification payload
        if (remoteMessage.notification != null) {
            // Notification payload
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body
            showNotification(title, body)
        } else if (remoteMessage.data.isNotEmpty()) {
            // Data payload
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        Log.d(TAG, "Title: ${title}")
        Log.d(TAG, "Body: ${body}")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channelName = "Channel Name"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.icon_rain)
        // set other notification properties

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

