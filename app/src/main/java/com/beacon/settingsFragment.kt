package com.beacon

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.beacon.R  // R 클래스의 경로를 정확히 수정해야 합니다.

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
        val btnSetDisaster: Button = view.findViewById(R.id.btn_setDisaster)
        btnSetDisaster.setOnClickListener {
            val intent = Intent(context, setDisasterActivity::class.java)
            startActivity(intent)
        }

        val btnSetLanguage: Button = view.findViewById(R.id.btn_setLanguage)
        btnSetDisaster.setOnClickListener {
            val intent = Intent(context, setLanguageMainActivity::class.java)
            startActivity(intent)
        }

        val btnSetGuildLine: Button = view.findViewById(R.id.btn_setGuildLine)
        btnSetDisaster.setOnClickListener {
            val intent = Intent(context, setGuildLineActivity::class.java)
            startActivity(intent)
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