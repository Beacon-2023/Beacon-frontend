package com.beacon.signup

import android.content.Context
import android.content.Intent
import com.beacon.basicStart.BaseActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.beacon.databinding.ActivitySignUpBinding
import com.beacon.databinding.ActivityStartBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType
import okhttp3.RequestBody

class signUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.btnNext.setOnClickListener {
            val userId= binding.inputId.text.toString()
            val userPw= binding.inputPw.text.toString()
            val userEmail= binding.inputEmail.text.toString()

            //null체크
            if(userId == "" || userPw == "" ||userEmail == "" ){
                runOnUiThread {
                    val alertDialog = AlertDialog.Builder(this@signUpActivity)
                        .setTitle("빈 칸이 존재합니다.")
                        .setMessage("모든 칸을 작성해주세요!")
                        .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                        .create()
                    alertDialog.show()
                }
            }
            else {
                checkDuplicate(userId,userPw,userEmail)
            }
        }

        binding.btnBack.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun checkDuplicate(userId: String, userPw: String, userEmail: String) {
        Log.d("회원가입", "이메일 중복여부 ${userEmail}")

        val client = OkHttpClient()
        val url_checkEmail = "http://43.202.105.197:8080/api/v1/members/duplicated/" + userId

        //요청에는 url주소 넣고
        val request = Request.Builder()
            .url(url_checkEmail)
            .get()
            .build()

        //요청을 큐에 넣자
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("회원가입", "실패했습니다.\n원인 : ${e}")
            }

            override fun onResponse(call: Call, response: Response) {
                //Log.d("회원가입", "응답이 존재합니다.\n응답 : ${response}")
                val responseBody = response.body()?.string()

                // 서버 응답에 따른 처리
                if (response.isSuccessful) {
                    runOnUiThread {
                        Log.d("회원가입", "이메일 : 가입 가능\n응답 : ${response}")
                        signUp(userId,userPw,userEmail)
                    }
                } else {
                    // 실패한 응답 처리
                    runOnUiThread {
                        val alertDialog = AlertDialog.Builder(this@signUpActivity)
                            .setTitle("이메일 중복")
                            .setMessage("이메일이 중복됩니다. 다른 이메일을 사용해주세요")
                            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                            .create()
                        alertDialog.show()
                    }
                }
            }
        })
    }

    private fun signUp(userId: String, userPw: String, userEmail: String) {
        Log.d("회원가입", "================================================")
        Log.d("회원가입", "ID: $userId PW: $userPw Email: $userEmail")

        val client = OkHttpClient()

        val url = "http://43.202.105.197:8080/api/v1/members"

        val json = JSONObject().apply {
            put("userId", userId)
            put("password", userPw)
            put("email", userEmail)
        }

        val mediaType = MediaType.parse("application/json")
        val requestBody = RequestBody.create(mediaType, json.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("회원가입", "실패: 반응없음.\nReason: ${e}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()?.string()

                if (response.isSuccessful) {
                    runOnUiThread {
                        Log.d("회원가입", "반응 존재 : 성공.\nResponse: ${response}")

                        //<-------------------로컬에 회원가입 내역 저장----------------->
                        val sharedPreferences = getSharedPreferences("user_Information", Context.MODE_PRIVATE)

                        val alertDialog = AlertDialog.Builder(this@signUpActivity)
                            .setTitle("로그아웃")
                            .setMessage("로그인 페이지로 이동합니다!")
                            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss()
                                // Navigate to the main activity here
                                val intent = Intent(this@signUpActivity, ActivityStartBinding::class.java)
                                startActivity(intent)
                                finish() // Optional, depending on your navigation flow
                            }
                            .create()
                        alertDialog.show()

                        // Use Editor to store values
                        val editor = sharedPreferences.edit()
                        editor.putString("ID", userId)
                        editor.putString("password", userPw)
                        editor.apply()
                    }
                }
                else {
                    Log.d("회원가입", "반응 존재 : 실패!.\nResponse: ${response}")
                    runOnUiThread {
                        val alertDialog = AlertDialog.Builder(this@signUpActivity)
                            .setTitle("회원가입 실패")
                            .setMessage("이미 존재하는 ID 입니다.")
                            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                            .create()
                        alertDialog.show()
                    }
                }
            }
        })
    }
}