package com.beacon.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.beacon.R
import com.beacon.databinding.ActivityStartBinding
import com.beacon.settings.disaster.setDisasterActivity
import com.beacon.settings.guildLine.setGuildLineActivity
import com.beacon.settings.language.setLanguageActivity

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

        val btnSetGuildLine: TextView = view.findViewById(R.id.btn_setGuildLine)
        btnSetGuildLine.setOnClickListener {
            val intent = Intent(context, setGuildLineActivity::class.java)
            startActivity(intent)
        }

        val btnSetLogout : Button = view.findViewById(R.id.btn_logout)
        btnSetLogout.setOnClickListener{
            //sharedPreferences에 저장된 ID,password 값을 null로 변경하고 ActivityStartBinding로 이동할 수 있도록
            val sharedPreferences = requireContext().getSharedPreferences("user_Information", Context.MODE_PRIVATE)

            // Clear the stored user credentials
            val editor = sharedPreferences.edit()
            editor.putString("ID", null)
            editor.putString("password", null)
            editor.apply()

            //API요청해야함..로그아웃!
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("로그아웃 성공")
                .setMessage("메인페이지로 이동합니다!")
                .setPositiveButton("확인") { dialog, _ -> dialog.dismiss()
                    // Navigate to the main activity here
                    val intent = Intent(requireContext(), ActivityStartBinding::class.java)
                    startActivity(intent)
                }
                .create()
            alertDialog.show()
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