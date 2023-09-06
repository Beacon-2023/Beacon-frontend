package com.beacon.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.beacon.R
import com.beacon.communication.MyOkHttpClient
import com.beacon.databinding.ActivityStartBinding
import com.beacon.settings.disaster.setDisasterActivity
import com.beacon.settings.guildLine.setGuildLineActivity
import com.beacon.settings.language.setLanguageActivity
import com.beacon.startActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class settingsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 버튼에 OnClickListener 설정
        val btnSetDisaster: TextView = view.findViewById(R.id.btn_setDisaster)
        btnSetDisaster.setOnClickListener {
            val intent = Intent(context, setDisasterActivity::class.java)
            startActivity(intent)
        }

        val btnSetLanguage: TextView = view.findViewById(R.id.btn_setLanguage)
        btnSetLanguage.setOnClickListener {
            val intent = Intent(context, setLanguageActivity::class.java)
            startActivity(intent)
        }

        //비회원이면 아예 못 누르게
        val btnSetGuildLine: TextView = view.findViewById(R.id.btn_setGuildLine)

        val sharedPreferences = requireContext().getSharedPreferences("user_Information", Context.MODE_PRIVATE)
        val savedUserID  = sharedPreferences.getString("ID", null)
        val savedPassword  = sharedPreferences.getString("password", null)

        val txt_fail = getString(R.string.fail_title)
        val txt_fail_reasno = getString(R.string.fail_you_notUser)

        btnSetGuildLine.setOnClickListener {
            if(savedUserID == null){
                requireActivity().runOnUiThread {
                    val alertDialog = AlertDialog.Builder(requireActivity())
                        .setTitle(txt_fail)
                        .setMessage(txt_fail_reasno)
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                            // Your code here
                        }
                        .create()
                    alertDialog.show()
                }
            }
            else{
                val intent = Intent(context, setGuildLineActivity::class.java)
                startActivity(intent)
            }
        }


        val dia_suc = getString(R.string.dialog_logout_gomain)
        val dia_logout = getString(R.string.txt_logout)

        val btnSetLogout : Button = view.findViewById(R.id.btn_logout)
        btnSetLogout.setOnClickListener{
                    val sharedPreferences = requireContext().getSharedPreferences("user_Information", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("ID", null)
                    editor.putString("password", null)
                    editor.apply()

                    Log.d("로그아웃", "START")
                    val client = OkHttpClient()
                    val url_logout = "http://43.202.105.197:8080/api/v1/members/logout"

                    //요청에는 url주소 넣고
                    val request = Request.Builder()
                        .url(url_logout)
                        .get()
                        .build()

                    //요청을 큐에 넣자
                    MyOkHttpClient.instance.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.d("로그아웃", "실패했습니다.\n원인 : ${e}")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            Log.d("로그아웃", "응답이 존재합니다.\n응답 : ${response}")
                            val responseBody = response.body?.string()

                            // 서버 응답에 따른 처리
                            if (response.isSuccessful) {
                                Log.d("", "로그아웃 : 성공 : ${response}")

                                requireActivity().runOnUiThread {
                                    val alertDialog = AlertDialog.Builder(requireActivity())
                                        .setTitle(dia_logout)
                                        .setMessage(dia_suc)
                                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                                            val sharedPreferences = requireContext().getSharedPreferences("user_Information", Context.MODE_PRIVATE)
                                            val editor = sharedPreferences.edit()
                                            editor.putString("ID", null)
                                            editor.putString("password", null)
                                            editor.apply()

                                            val intent = Intent(requireContext(), startActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                        .create()
                                    alertDialog.show()
                                }
                            }
                            else {
                                // 실패한 응답 처리
                                Log.d("", "로그아웃 : 실패 : ${response}")
                                requireActivity().runOnUiThread {
                                    val alertDialog = AlertDialog.Builder(requireActivity())
                                        .setTitle(txt_fail)
                                        .setMessage(txt_fail_reasno)
                                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss()
                                            // Your code here
                                        }
                                        .create()
                                    alertDialog.show()
                                }
                            }
                        }
                    })
                }
        }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            settingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}