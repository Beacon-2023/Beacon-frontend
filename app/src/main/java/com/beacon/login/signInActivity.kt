package com.beacon.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.beacon.Constants
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
        Log.d("로그인", "로그인 시도")
        Log.d("로그인", "ID : ${userId} PW : ${userPw}")
        val client = OkHttpClient()
        val url_login = "/member-controller/login"

        //요청 바디에 id,pw 담고
        val requestBody = FormBody.Builder()
            .add("userId", userId)
            .add("userPw", userPw)
            .build()

        //요청에는 url주소 넣고
        val request = Request.Builder()
            .url(url_login)
            .post(requestBody)
            .build()

        //요청을 큐에 넣자
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("로그인", "실패했습니다.\n원인 : ${e}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("로그인", "응답이 존재합니다.\n응답 : ${response}")
                val responseBody = response.body()?.string()

                // 서버 응답에 따른 처리
                if (response.isSuccessful) {
                    runOnUiThread {
                        Log.d("로그인", "성공적 응답입니다.\n응답 : ${response}")
                        val intent = Intent(this@signInActivity, NaviActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    // 실패한 응답 처리
                    runOnUiThread {
                        // Display a toast message for login failure
                        //Toast.makeText(this@signInActivity, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()

                        // Or show an error dialog
                        val alertDialog = AlertDialog.Builder(this@signInActivity)
                            .setTitle("로그인 실패")
                            .setMessage("로그인에 실패했습니다. 다시 시도해주세요.")
                            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                            .create()
                        alertDialog.show()
                    }
                }
            }
        })
    }
}