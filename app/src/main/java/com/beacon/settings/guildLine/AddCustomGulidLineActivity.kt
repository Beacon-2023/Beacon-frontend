package com.beacon.settings.guildLine

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.beacon.R
import com.beacon.basicStart.BaseActivity
import com.beacon.communication.MyOkHttpClient
import com.beacon.databinding.ActivityAddCustomGulidLineBinding
import com.beacon.databinding.ActivityStartBinding
import com.beacon.login.signInActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AddCustomGulidLineActivity : BaseActivity() {
    private lateinit var binding: ActivityAddCustomGulidLineBinding
    var have_inf = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomGulidLineBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val clickedIndex = intent.getIntExtra("Idx", -1)
        var disaster = ""

        when (clickedIndex) {
            0 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_heavy_rain)+ " 가이드라인"
                disaster = "FLOOD"
            }
            1 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_Earthquake) + " 가이드라인"
                disaster = "EARTHQUAKE"
            }
            2 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_Typhoon) + " 가이드라인"
                disaster = "TYPHOON"
            }
            3 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_forest_fires) + " 가이드라인"
                disaster = "WILDFIRE"
            }
            4 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_civil_defense) + " 가이드라인"
                disaster = "CIVIL_DEFENCE"
            }
            else -> {
                disaster = "etc"
            }
        }


        //<ㅔ------------------불러오는 부분------------------------->
        Log.d("가이드라인", "==============================================")
        Log.d("가이드라인", "커스텀 가이드 : 불러오기 시도")

        val url = "http://43.202.105.197:8080/api/v1/guidelines/customs/" + disaster

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        //불러오기 통신
        MyOkHttpClient.instance.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("가이드라인", "커스텀 가이드 : 불러오기 통신 실패.\n원인 : ${e}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    val title = json.getString("title")
                    val content = json.getString("content")

                    Log.d("가이드라인", "커스텀 가이드 : 불러오기 성공 \n응답 : ${response}")
                    Log.d("가이드라인", "$title | $content")
                    binding.writeGuildLine.setText(content)
                    binding.btnSave.setText("수정")

                    binding.btnSave.setOnClickListener {
                        val title = "커스텀 가이드라인"
                        val content = binding.writeGuildLine.text.toString()

                        Log.d("커스텀 가이드", "====================수  정============================")
                        Log.d("커스텀 가이드", "제목 : $title | 내용 : $content | 재난 : $disaster")

                        //val client = OkHttpClient()
                        val url = "http://43.202.105.197:8080/api/v1/guidelines/customs"

                        val json = JSONObject().apply {
                            put("title", title)
                            put("content", content)
                            put("disaster", disaster)
                        }

                        val mediaType = "application/json".toMediaTypeOrNull()
                        val requestBody = RequestBody.create(mediaType, json.toString())
                        val request = Request.Builder()
                            .url(url)
                            .put(requestBody)
                            .build()

                        MyOkHttpClient.instance.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                Log.d("커스텀 가이드", "실패: 반응없음.\nReason: ${e}")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val responseBody = response.body?.string()

                                if (response.isSuccessful) {
                                    Log.d("커스텀 가이드", "반응 존재 : 성공.\nResponse: ${response}")
                                    runOnUiThread {
                                        val alertDialog = AlertDialog.Builder(this@AddCustomGulidLineActivity)
                                            .setTitle(getString(R.string.guild_rule))
                                            .setMessage(getString(R.string.dialog_title))
                                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                                                // Your code here
                                            }
                                            .create()
                                        alertDialog.show()
                                    }
                                }
                                else {
                                    Log.d("커스텀 가이드", "반응 존재 : 실패!.\nResponse: ${response}")
                                }
                            }
                        })
                    }

                } else {
                    Log.d("가이드라인", "커스텀 가이드 : 불러오기 실패\n응답 : ${response}")
                    have_inf = 0
                }
            }
        })

        //<---------------수정하는 부분
        if(have_inf == 1){

        }
        else{
            //가이드라인 새로 생성.
            binding.btnSave.setOnClickListener {
                val title = "커스텀 가이드라인"
                val content = binding.writeGuildLine.text.toString()

                Log.d("커스텀 가이드", "================================================")
                Log.d("커스텀 가이드", "제목 : $title | 내용 : $content | 재난 : $disaster")

                //val client = OkHttpClient()
                val url = "http://43.202.105.197:8080/api/v1/guidelines/customs"

                val json = JSONObject().apply {
                    put("title", title)
                    put("content", content)
                    put("disaster", disaster)
                }

                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = RequestBody.create(mediaType, json.toString())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                MyOkHttpClient.instance.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("커스텀 가이드", "실패: 반응없음.\nReason: ${e}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()

                        if (response.isSuccessful) {
                            Log.d("커스텀 가이드", "반응 존재 : 성공.\nResponse: ${response}")

                            runOnUiThread {
                                val alertDialog = AlertDialog.Builder(this@AddCustomGulidLineActivity)
                                    .setTitle(getString(R.string.guild_rule))
                                    .setMessage(getString(R.string.dialog_title))
                                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                                        // Your code here
                                    }
                                    .create()
                                alertDialog.show()
                            }
                        }
                        else {
                            Log.d("커스텀 가이드", "반응 존재 : 실패!.\nResponse: ${response}")
                            runOnUiThread {

                            }
                        }
                    }
                })
            }
        }
        }
}
