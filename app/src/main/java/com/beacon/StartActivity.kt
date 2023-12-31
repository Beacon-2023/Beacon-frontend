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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.beacon.communication.MyOkHttpClient
import com.beacon.data.AppDatabase
import com.beacon.data.DataRepository
import com.beacon.databinding.ActivityStartBinding
import com.beacon.login.signInActivity
import com.beacon.signup.signUpActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

class startActivity : BaseActivity() {
    private lateinit var binding: ActivityStartBinding
    val LOCATION_PERMISSION_REQUEST_CODE = 101

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 공유 프리퍼런스를 가져옵니다.
        val sharedPreferences = getSharedPreferences("user_Information", Context.MODE_PRIVATE)
        val savedUserID  = sharedPreferences.getString("ID", null)
        val savedPassword  = sharedPreferences.getString("password", null)
        Log.d("자동 로그인", "$savedUserID | $savedPassword")
        if(savedUserID != null) {
            AutoLogin(savedUserID, savedPassword)
        }

        else{
            binding = ActivityStartBinding.inflate(layoutInflater)
            var view = binding.root
            setContentView(view)

            //권한 가져오기
            getPermission()

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

            //알림 받고 싶은 재난 DB 초기화 & 첫 진입 시 토큰 서버에 전송
            val dataDao = AppDatabase.getDatabase(this).dataDao()
            val dataRepository = DataRepository(dataDao)

            CoroutineScope(Dispatchers.IO).launch {
                //<--------ID 저장 여부에 따른 토큰 전송--------->
                if(savedUserID == null){
                    //Log.d("테스트", "[빈 ID] : 토큰만 전송합니다.")
                    //sendToken("")
                } else{
                    //Log.d("테스트", "[ID + 토큰] ${savedUserID}")
                    //sendToken(savedUserID)
                }

                //DB가 비어있는 첫 접속 => DB 세팅
                val existingData = dataRepository.getAllData()
                if (existingData.isEmpty()) {
                    dataRepository.insertInitialData()

                }
            }

            //PAPAGO API
//            Log.d("번역", "오늘의 테스트")
//            translateWithNmtApi("오늘의 테스트", "en")
//            translateWithNmtApi("오늘의 테스트", "ja")
//            translateWithNmtApi("오늘의 테스트", "th")
//            translateWithNmtApi("오늘의 테스트", "zh-CN")
        }


    }

    private fun sendToken(ID : String) {
        Log.d("테스트", "토큰 집어넣기 실행")
        val url = "http://43.202.105.197:8080/api/v1/token"

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
            put("token", fcm_tkn)
            put("userName", ID)
        }

        val mediaType = "application/json".toMediaTypeOrNull()
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
    }

    private fun AutoLogin(savedUserId: String?, savedUserPassword: String?) {
        //비어있지 않다면!
        if (savedUserId != null && savedUserPassword != null) {
            Log.d("자동 로그인", "로그인을 시도합니다 ID: $savedUserId PW: $savedUserPassword")

            val client = OkHttpClient()

            val url = "http://43.202.105.197:8080/api/v1/members/login"

            val json = JSONObject().apply {
                put("userName", savedUserId)
                put("password", savedUserPassword)
            }

            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = RequestBody.create(mediaType, json.toString())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            MyOkHttpClient.instance.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("자동 로그인", "Failed.\nReason: ${e}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()

                    if (response.isSuccessful) {
                        runOnUiThread {
                            val intent = Intent(this@startActivity, NaviActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        runOnUiThread {
                            Log.d("자동 로그인", "응답[실패].\n응답: ${responseBody}")

                            val alertDialog = AlertDialog.Builder(this@startActivity)
                                .setTitle("자동 로그인 실패")
                                .setMessage("서버가 불안정하여 자동 로그인 정보를 삭제합니다. 다시 로그인 부탁드립니다.")
                                .setPositiveButton("확인") { dialog, _ -> dialog.dismiss()
                                    //sharedPreferences에 저장된 ID,password 값을 null로 변경하고 ActivityStartBinding로 이동할 수 있도록
                                    val sharedPreferences = getSharedPreferences("user_Information", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("ID", null)
                                    editor.putString("password", null)
                                    editor.apply()

                                    val intent = Intent(this@startActivity, startActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .create()
                            alertDialog.show()
                        }
                    }
                }
            })
        }
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

    private fun getPermission() {
        // Android 12 이상에서 배터리 최적화 무시 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        // Android 13 (Tiramisu) 이상에서 POST_NOTIFICATIONS 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult.launch(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            )
        }

        // 위치 접근 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // 위치 접근 권한이 이미 허용되어 있는 경우 처리할 내용 추가
        }

        // 백그라운드 위치 접근 권한 확인 및 요청
        val permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (permissionCheck2 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 0)
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 접근 권한이 허용된 경우 처리할 내용 추가
            } else {
                // 위치 접근 권한이 거부된 경우 처리할 내용 추가
            }
        }
    }

    // 여러 권한 요청 결과 처리
    private val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> {
                // 거부된 권한 목록을 가져와 처리
                val map = deniedPermissionList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                map[DENIED]?.let {
                    // 권한 요청이 이유와 함께 거부된 경우 처리
                }
                map[EXPLAINED]?.let {
                    // 권한 요청이 이유와 함께 거부되지 않은 경우 처리
                }
            }
            else -> {
                // 모든 권한이 허용된 경우 처리
            }
        }
    }

}
