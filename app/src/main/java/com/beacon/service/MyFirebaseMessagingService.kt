package com.beacon.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.beacon.R
import com.beacon.settings.guildLine.viewGuildLineActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "번역"
        val notificationId = 123 // Use a value that makes sense for your app
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "==================================================")
        Log.d(TAG, "FCM 수신 확인")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (remoteMessage.notification != null) {
            // Notification payload
            Log.d(TAG, "notification")
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body

            if (title != null && body != null) {
                val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
                val currentLocale = sharedPreferences.getString("My_Lang", "")

                // 백그라운드 스레드 또는 코루틴을 사용하여 번역 서비스 호출
                GlobalScope.launch(Dispatchers.IO) {
                    val translatedTitle = translateWithNmtApi(title, currentLocale.toString())
                    val translatedBody = translateWithNmtApi(body, currentLocale.toString())

                    // UI 스레드에서 알림을 표시
                    withContext(Dispatchers.Main) {
                        showNotification(translatedTitle, translatedBody)
                    }
                }
            }
        }
        else if (remoteMessage.data.isNotEmpty()) {
            // Data payload
            Log.d(TAG, "data")
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]

            if (title != null && body != null) {
                val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
                val currentLocale = sharedPreferences.getString("My_Lang", "")

                // 백그라운드 스레드 또는 코루틴을 사용하여 번역 서비스 호출
                GlobalScope.launch(Dispatchers.IO) {
                    val translatedTitle = translateWithNmtApi(title, currentLocale.toString())
                    val translatedBody = translateWithNmtApi(body, currentLocale.toString())

                    // UI 스레드에서 알림을 표시
                    withContext(Dispatchers.Main) {
                        showNotification(translatedTitle, translatedBody)
                    }
                }
            }
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

        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val currentLocale = sharedPreferences.getString("My_Lang", "")
        Log.d("번역", "현재 로케일 : ${currentLocale}")

        // Coroutine scope
        GlobalScope.launch(Dispatchers.Main) {
            // Perform translation in a coroutine
            val translatedTitle = translateWithNmtApi(title.toString(), currentLocale.toString())
            val translatedBody = translateWithNmtApi(body.toString(), currentLocale.toString())

            // Continue building the notification with translatedTitle and translatedBody
            val intent = Intent(this@MyFirebaseMessagingService, viewGuildLineActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this@MyFirebaseMessagingService, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            val notificationBuilder = NotificationCompat.Builder(this@MyFirebaseMessagingService, "channel_id")
                .setContentTitle(translatedTitle)
                .setContentText(translatedBody)
                .setSmallIcon(R.drawable.icon_beacon)
                .setContentIntent(pendingIntent)

            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }


    private suspend fun translateWithNmtApi(value: String, toLanguage: String): String {
        val clientId = "rEQ9nMHXaly9DJjySccs" // Replace with your application client ID
        val clientSecret = "dzCeXwa26O" // Replace with your application client secret

        val apiURL = "https://openapi.naver.com/v1/papago/n2mt"
        val text: String
        try {
            text = URLEncoder.encode(value, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("Encoding failed", e)
        }

        val requestHeaders = mutableMapOf<String, String>()
        requestHeaders["X-Naver-Client-Id"] = clientId
        requestHeaders["X-Naver-Client-Secret"] = clientSecret

        return withContext(Dispatchers.IO) {
            val responseBody = post(apiURL, requestHeaders, text, toLanguage)
            Log.d("번역", responseBody)

            val jsonObject = JSONObject(responseBody)
            val translatedText = jsonObject.getJSONObject("message")
                .getJSONObject("result")
                .getString("translatedText")

            Log.d("번역", translatedText)
            translatedText // Return the translated text
        }
    }

    private fun post(apiUrl: String, requestHeaders: Map<String, String>, text: String, toLanguage : String): String {
        val con = connect(apiUrl)
        val postParams = "source=ko&target=$toLanguage&text=$text" // <-------여기서 번역 어떻게 할 지 !!!!------>
        try {
            con.requestMethod = "POST"
            for ((key, value) in requestHeaders) {
                con.setRequestProperty(key, value)
            }

            con.doOutput = true
            DataOutputStream(con.outputStream).use { wr ->
                wr.write(postParams.toByteArray())
                wr.flush()
            }

            val responseCode = con.responseCode
            return if (responseCode == HttpURLConnection.HTTP_OK) {
                readBody(con.inputStream)
            } else {
                readBody(con.errorStream)
            }
        } catch (e: IOException) {
            throw RuntimeException("API request and response failed", e)
        } finally {
            con.disconnect()
        }
    }

    private fun connect(apiUrl: String): HttpURLConnection {
        return try {
            val url = URL(apiUrl)
            url.openConnection() as HttpURLConnection
        } catch (e: MalformedURLException) {
            throw RuntimeException("The API URL is invalid: $apiUrl", e)
        } catch (e: IOException) {
            throw RuntimeException("Connection failed: $apiUrl", e)
        }
    }

    private fun readBody(body: InputStream): String {
        InputStreamReader(body).use { streamReader ->
            BufferedReader(streamReader).use { lineReader ->
                val responseBody = StringBuilder()
                var line: String?
                while (lineReader.readLine().also { line = it } != null) {
                    responseBody.append(line)
                }
                return responseBody.toString()
            }
        }
    }
}

