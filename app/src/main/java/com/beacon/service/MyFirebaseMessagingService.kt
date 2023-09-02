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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "테스트"
        val notificationId = 123 // Use a value that makes sense for your app
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "==================================================")
        Log.d(TAG, "FCM 수신 확인")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (remoteMessage.notification != null) {
            // Notification payload
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body
            showNotification(title, body)
        }

        else if (remoteMessage.data.isNotEmpty()) {
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

        val currentLocale = resources.configuration.locale

        if(currentLocale.toString() == "ja"){
            translateWithNmtApi(title.toString(), "ja")
        }
        else if(currentLocale.toString() == "zh"){
            translateWithNmtApi(title.toString(), "zh")
        }
        else if(currentLocale.toString() == "en"){
            translateWithNmtApi(title.toString(), "en")
        }
        else if(currentLocale.toString() == "th"){
            translateWithNmtApi(title.toString(), "th")
        }

        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.icon_rain)
        // set other notification properties

        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    private fun translateWithNmtApi(value : String, toLanguage : String) {
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

        // Perform network operation using a coroutine on IO dispatcher
        GlobalScope.launch(Dispatchers.IO) {
            val responseBody = post(apiURL, requestHeaders, text, toLanguage)
            Log.d("번역", responseBody)

            val jsonObject = JSONObject(responseBody)
            val translatedText = jsonObject.getJSONObject("message")
                .getJSONObject("result")
                .getString("translatedText")

            Log.d("번역", translatedText)
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

