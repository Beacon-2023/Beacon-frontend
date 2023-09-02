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
import com.beacon.databinding.ActivitySignInBinding
import com.beacon.signup.signUpActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class signInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    var isCheckedAuto = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        var view = binding.root

        setContentView(view)

        binding.btnLoginToSignup.setOnClickListener{
            val intent = Intent(this, signUpActivity::class.java)
            startActivity(intent)
        }

        binding.isCheckedAutoLogin.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) {
                isCheckedAuto = 1
            }else{
                isCheckedAuto = 0
            }
        }

        //로그인 처리 로직 구현하기
        binding.btnSignin.setOnClickListener{
            val userId= binding.inputId.text.toString()
            val userPw= binding.inputPw.text.toString()

            //null체크
            if(userId == "" || userPw == ""){
                runOnUiThread {
                    val alertDialog = AlertDialog.Builder(this@signInActivity)
                        .setTitle("빈 칸이 존재합니다.")
                        .setMessage("모든 칸을 작성해주세요!")
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

        val client = OkHttpClient()

        val url = "http://43.202.105.197:8080/api/v1/members/login"

        val json = JSONObject().apply {
            put("userName", userId)
            put("password", userPw)
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
                        //<-------------------로컬에 회원가입 내역 저장----------------->
                        //[ADD] : 자동 로그인이 체크 여부 확인!
                        if(isCheckedAuto == 1){
                            Log.d("로그인", "[자동 로그인] : O")
                            val sharedPreferences = getSharedPreferences("user_Information", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("ID", userId)
                            editor.putString("password", userPw)
                            editor.apply()
                        }
                        val intent = Intent(this@signInActivity, NaviActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    runOnUiThread {
                        Log.d("로그인", "[실패] 응답 존재\n응답: ${responseBody}")

                        val alertDialog = AlertDialog.Builder(this@signInActivity)
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