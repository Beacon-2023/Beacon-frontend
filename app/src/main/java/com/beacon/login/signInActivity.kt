package com.beacon.login
import android.content.Context
import com.beacon.basicStart.BaseActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.beacon.NaviActivity
import com.beacon.R
import com.beacon.communication.MyOkHttpClient
import com.beacon.databinding.ActivitySignInBinding
import com.beacon.signup.signUpActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class signInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    var isCheckedAuto = 0

    lateinit var login : String
    lateinit var fail_login : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        var view = binding.root

        setContentView(view)

        binding.btnLoginToSignup.setOnClickListener{
            val intent = Intent(this, signUpActivity::class.java)
            startActivity(intent)
        }


        val title = getString(R.string.dialog_blank)
        val message = getString(R.string.dialog_blank_txt)
        login = getString(R.string.txt_login)
        fail_login = getString(R.string.login_fail)

        //로그인 처리 로직 구현하기
        binding.btnSignin.setOnClickListener{
            val userId= binding.inputId.text.toString()
            val userPw= binding.inputPw.text.toString()

            //null체크
            if(userId == "" || userPw == ""){
                runOnUiThread {
                    val alertDialog = AlertDialog.Builder(this@signInActivity)
                        .setTitle(R.string.dialog_blank)
                        .setMessage(R.string.dialog_blank_txt)
                        .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                        .create()
                    alertDialog.show()
                }
            }
            else {
                loginUser(userId,userPw)
            }
        }
    }

    private fun loginUser(userId: String, userPw: String) {
        Log.d("로그인", "로그인을 시도합니다 ID: $userId PW: $userPw")

        val url = "http://43.202.105.197:8080/api/v1/members/login"

        val json = JSONObject().apply {
            put("userName", userId)
            put("password", userPw)
        }

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, json.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        MyOkHttpClient.instance.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("로그인", "Failed.\nReason: ${e}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful) {
                    runOnUiThread {
                        Log.d("로그인", "[성공] 응답 존재\n응답: ${response}")
                        //<-------------------로컬에 회원가입 내역 저장----------------->
                        //[ADD] : 자동 로그인이 체크 여부 확인!
                            Log.d("로그인", "[자동 로그인] : O")
                            val sharedPreferences = getSharedPreferences("user_Information", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("ID", userId)
                            editor.putString("password", userPw)
                            editor.apply()

                        val intent = Intent(this@signInActivity, NaviActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Log.d("로그인", "[실패] 응답 존재\n응답: ${responseBody}")

                        val alertDialog = AlertDialog.Builder(this@signInActivity)
                            .setTitle(login)
                            .setMessage(fail_login)
                            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                            .create()
                        alertDialog.show()
                    }
                }
            }
        })
    }
}
