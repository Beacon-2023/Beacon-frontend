package com.beacon.settings.guildLine

import android.annotation.SuppressLint
import android.content.ContentProviderOperation.newCall
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.beacon.R
import com.beacon.basicStart.BaseActivity
import com.beacon.communication.MyOkHttpClient
import com.beacon.databinding.ActivityViewGuildLineBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
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


class viewGuildLineActivity : BaseActivity() {
    lateinit var binding : ActivityViewGuildLineBinding
    private val rankItemList: MutableList<RankItem> = ArrayList()
    private lateinit var adapter: RankListAdapter
    private lateinit var nameList: MutableList<String>

    lateinit var txt_rule: String
    lateinit var txt_safezone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGuildLineBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)
        nameList = mutableListOf()

        //val what_disaster = intent.getStringExtra("disaster")
        val listView = findViewById<ListView>(com.beacon.R.id.listView_safezone)

        //TODO : API로 가이드라인 받기
        getGuilidLine()

        //TODO :받아온 JSON형태를 풀어서 나타내자
        getShelter()

        rankItemList.add(RankItem(com.beacon.R.drawable.icon_1, "대피소1"))
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_2, "대피소2"))
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_3, "대피소3"))
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_4, "대피소4"))
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_5, "대피소5"))

        adapter = RankListAdapter(this, rankItemList)
        listView.adapter = adapter
    }

    private fun getGuilidLine() {
        // 공유 프리퍼런스를 가져옵니다.
        val sharedPreferences = getSharedPreferences("user_Information", Context.MODE_PRIVATE)
        val savedUserID  = sharedPreferences.getString("ID", null)
        val savedPassword  = sharedPreferences.getString("password", null)
        var judge = 0
        var text = ""
        val url = "http://43.202.105.197:8080/api/v1/guidelines/customs/" + "FLOOD"

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

                    Log.d("가이드라인", "=================================================")
                    Log.d("가이드라인", "커스텀 가이드 : 불러오기 성공 \n응답 : ${response}")
                    Log.d("가이드라인", "$title | $content")
                    judge = 1;
                    text = content
                    runOnUiThread{
                        binding.textGuildLine.setText(text)
                    }

                } else {
                    Log.d("가이드라인", "커스텀 가이드 : 불러오기 실패\n응답 : ${response}")
                }
            }
        })

        if(savedUserID != null && judge == 1){
            //Log.d("가이드라인", "반환값 : $text")

        }
        //로그인 했지만 커스텀 설정 안한 || 로그인을 안한 경우
        else{
            val client = OkHttpClient()
            val url = "http://43.202.105.197:8080/api/v1/guidelines/" + "FLOOD"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("가이드라인", "가이드 : 실패했습니다.\n원인 : ${e}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()

                    if (response.isSuccessful && responseBody != null) {
                        val json = JSONObject(responseBody)
                        val title = json.getString("title")
                        val content = json.getString("content")

                        Log.d("가이드라인", "가이드 : 성공 응답이 존재합니다.\n응답 : ${response}")
                        Log.d("가이드라인", "$title $content")
                        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
                        val currentLocale = sharedPreferences.getString("My_Lang", "")
                        Log.d("번역", "현재 로케일 : ${currentLocale}")

                        if(currentLocale.toString() != "ko"){
                            translateWithNmtApi(content, currentLocale.toString()) { translatedText ->
                                runOnUiThread {
                                    if(translatedText != null)
                                        binding.textGuildLine.setText(translatedText)
                                }
                            }
                        }

                        runOnUiThread{
                            binding.textGuildLine.setText(content)
                        }
                    } else {
                        Log.d("가이드라인", "가이드 : 실패 응답이 존재합니다.\n응답 : ${response}")
                    }
                }
            })
        }
    }

    private fun getShelter() {
        val client = OkHttpClient()
        val url = "http://43.202.105.197:8080/api/v1/shelters"

        //"CIVIL_DEFENCE/WILDFIRE/TYPHOON/FLOOD/EARTHQUAKE/ETC"
        val json = JSONObject().apply {
            put("x", 36.1390378)
            put("y", 128.4159963)
            put("count", 5)
            put("shelterCategory", "EARTHQUAKE")
        }

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, json.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Log.d("가이드라인[대피소]", "============================================")
        //요청을 큐에 넣자
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("가이드라인[대피소]", "대피소 : 응답 자체가 실패했습니다.\n원인 : ${e}")
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                    runOnUiThread {
                        Log.d("가이드라인[대피소]", "Success\nResponse: ${response}")
                        Log.d("가이드라인[대피소]", "body : ${responseBody}")

                        try {
                            val jsonArray = JSONArray(responseBody)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val name = jsonObject.getString("name")
                                Log.d("가이드라인[대피소]", "$i name")
                                runOnUiThread {
                                    rankItemList[i].setRankName(name)
                                }
                                adapter.notifyDataSetChanged()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
        })
    }

    private fun translateWithNmtApi(
        value: String,
        toLanguage: String,
        callback: (String) -> Unit
    ) {
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

        Thread {
            val responseBody = post(apiURL, requestHeaders, text, toLanguage)
            Log.d("번역", responseBody)

            val jsonObject = JSONObject(responseBody)
            val translatedText = jsonObject.getJSONObject("message")
                .getJSONObject("result")
                .getString("translatedText")

            Log.d("번역", translatedText)
            callback(translatedText)
        }.start()
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