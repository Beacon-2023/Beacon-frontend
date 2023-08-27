package com.beacon

import com.beacon.basicStart.BaseActivity
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.beacon.data.AppDatabase
import com.beacon.data.DataRepository
import com.beacon.databinding.ActivityStartBinding
import com.beacon.login.signInActivity
import com.beacon.signup.signUpActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class startActivity : BaseActivity() {
    private lateinit var binding: ActivityStartBinding
    val MY_PERMISSION_ACCESS_ALL = 100
    val LOCATION_PERMISSION_REQUEST_CODE = 101

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)
        getPermission()

        // 공유 프리퍼런스를 가져옵니다.
        val sharedPreferences = getSharedPreferences("user_Information", Context.MODE_PRIVATE)
        val savedUserID  = sharedPreferences.getString("ID", null)
        val savedPassword  = sharedPreferences.getString("password", null)

        Log.d("자동 로그인", "ID: $savedUserID PW: $savedPassword")
        AutoLogin(savedUserID, savedPassword)

        //버튼 리스너
        binding.btnSignin.setOnClickListener {
            val intent = Intent(this, signInActivity::class.java)
            startActivity(intent)
        }

        binding.btnLoginToSignup.setOnClickListener {
            val intent = Intent(this, signUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnNologin.setOnClickListener {
            val intent = Intent(this, NaviActivity::class.java)
            startActivity(intent)
        }

        val dataDao = AppDatabase.getDatabase(this).dataDao()
        val dataRepository = DataRepository(dataDao)

        CoroutineScope(Dispatchers.IO).launch {
            val existingData = dataRepository.getAllData()
            if (existingData.isEmpty()) {
                dataRepository.insertInitialData()
            }
        }
    }

    private fun AutoLogin(savedUserId: String?, savedUserPassword: String?) {
        //비어있지 않다면!
        if (savedUserId != null && savedUserPassword != null) {
            Log.d("자동 로그인", "로그인을 시도합니다 ID: $savedUserId PW: $savedUserPassword")

            val client = OkHttpClient()

            val url = "http://43.202.105.197:8080/api/v1/members/login"

            val json = JSONObject().apply {
                put("userId", savedUserId)
                put("password", savedUserPassword)
            }

            val mediaType = MediaType.parse("application/json")
            val requestBody = RequestBody.create(mediaType, json.toString())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("로그인", "Failed.\nReason: ${e}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()

                    if (response.isSuccessful) {
                        runOnUiThread {
                            val intent = Intent(this@startActivity, NaviActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        runOnUiThread {
                            Log.d("로그인", "응답 성공 : 실패.\n응답: ${responseBody}")

                            val alertDialog = AlertDialog.Builder(this@startActivity)
                                .setTitle("로그인 실패")
                                .setMessage("입력하신 정보를 다시 확인해주세요!")
                                .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                                .create()
                            alertDialog.show()
                        }
                    }
                }
            })
        }
    }


    private fun getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Request ignore battery optimizations
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult.launch(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            )
        }

        // Check and request GPS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission already granted
        }

        // 백그라운드 퍼미션
        val permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (permissionCheck2 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 0)
        }
    }

    private fun post(apiUrl: String, requestHeaders: Map<String, String>, text: String): String {
        val con = connect(apiUrl)
        val postParams = "source=ko&target=en&text=$text" // Source language: Korean (ko) -> Target language: English (en)
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
            return if (responseCode == HttpURLConnection.HTTP_OK) { // normal response
                readBody(con.inputStream)
            } else { // error response
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
            throw RuntimeException("The API URL is invalid. : $apiUrl", e)
        } catch (e: IOException) {
            throw RuntimeException("Connection failed: $apiUrl", e)
        }
    }

    private fun readBody(body: InputStream): String {
        val streamReader = InputStreamReader(body)
        BufferedReader(streamReader).use { lineReader ->
            val responseBody = StringBuilder()

            var line: String?
            while (lineReader.readLine().also { line = it } != null) {
                responseBody.append(line)
            }

            return responseBody.toString()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }

    //권한 얻는
    private val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> {
                val map = deniedPermissionList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                map[DENIED]?.let {
                    // 단순히 권한이 거부 되었을 때
                }
                map[EXPLAINED]?.let {
                    // 권한 요청이 완전히 막혔을 때(주로 앱 상세 창 열기)
                }
            }
            else -> {
                // 모든 권한이 허가 되었을 때
            }
        }
    }

}
