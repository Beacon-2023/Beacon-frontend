package com.beacon.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beacon.NaviActivity
import com.beacon.databinding.ActivitySignInBinding
import com.beacon.signup.signUpActivity
import okhttp3.*
import java.io.IOException

class signInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.btnSignup.setOnClickListener{
            val intent = Intent(this, signUpActivity::class.java)
            startActivity(intent)
        }

        //로그인 처리 로직 구현하기
        binding.btnSignin.setOnClickListener{
            val userId= binding.inputId.text.toString()
            val userPw= binding.inputPw.text.toString()

            loginUser(userId,userPw)
        }
    }

    private fun loginUser(userId: String, userPw: String) {
        val client = OkHttpClient()
        val url = ""

        //요청 바디에 id,pw 담고
        val requestBody = FormBody.Builder()
            .add("userId", userId)
            .add("userPw", userPw)
            .build()

        //요청에는 url주소 넣고
        val request = Request.Builder()
            .url(url)  // 실제 서버 URL로 바꿔주세요
            .post(requestBody)
            .build()

        //요청을 큐에 넣자
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 서버 요청 실패 시 처리할 내용
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()

                // 서버 응답에 따른 처리
                if (response.isSuccessful) {
                    runOnUiThread {
                        val intent = Intent(this@signInActivity, NaviActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    // 실패한 응답 처리
                    runOnUiThread {
                        // 실패 페이지 표시 또는 오류 메시지 출력 등
                    }
                }
            }
        })
    }
}