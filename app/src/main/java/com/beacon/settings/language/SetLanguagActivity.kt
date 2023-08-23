package com.beacon.settings.language

import BaseActivity
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import com.beacon.R
import java.util.Locale

class setLanguageActivity : BaseActivity() {
    private lateinit var btnKorean: AppCompatButton
    private lateinit var btnChinese: AppCompatButton
    private lateinit var btnEnglish: AppCompatButton
    private lateinit var btnThai: AppCompatButton
    private lateinit var btnJp: AppCompatButton
    private lateinit var language_code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language_main)

        // 저장된 언어 코드 불러오기!
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (language != null) {
            Log.d("테스트", "language: $language")
            language_code = language
        }

        btnKorean = findViewById(R.id.btn_korean)
        btnChinese = findViewById(R.id.btn_중국어)
        btnEnglish = findViewById(R.id.btn_english)
        btnThai = findViewById(R.id.btn_태국어)
        btnJp = findViewById(R.id.btn_japanese)

        // 선택된 언어 코드는 R.drawable.border_radius_box_50radius 선택되지 않은 버튼들은 R.drawable.border_radius_box_50_notcheck
        when (language_code) {
            "ko" -> updateButtonColors(btnKorean)
            "ja" -> updateButtonColors(btnJp)
            "zh" -> updateButtonColors(btnChinese)
            "en" -> updateButtonColors(btnEnglish)
            "th" -> updateButtonColors(btnThai)
        }

        btnKorean.setOnClickListener {
            updateButtonColors(btnKorean)
            setLocale("ko")
            recreate()
        }

        btnJp.setOnClickListener {
            updateButtonColors(btnJp)
            setLocale("ja")
            recreate()
        }

        btnChinese.setOnClickListener {
            updateButtonColors(btnChinese)
            setLocale("zh")
            recreate()
        }

        btnEnglish.setOnClickListener {
            updateButtonColors(btnEnglish)
            setLocale("en")
            recreate()
        }

        btnThai.setOnClickListener {
            updateButtonColors(btnThai)
            setLocale("th")
            recreate()
        }
    }

    private fun updateButtonColors(selectedButton: AppCompatButton) {
        val buttons = arrayOf(btnKorean, btnChinese, btnEnglish, btnThai, btnJp)

        for (button in buttons) {
            val isSelected = button == selectedButton

            button.setBackgroundResource(
                if (isSelected) {
                    R.drawable.border_radius_box_50radius
                } else {
                    R.drawable.border_radius_box_50_notcheck
                }
            )

            button.setTextColor(
                if (isSelected) {
                    ContextCompat.getColor(this, R.color.white)
                } else {
                    ContextCompat.getColor(this, R.color.black)
                }
            )
        }
    }

    private fun setLocale(lang: String) {
        Log.d("테스트", "setLocale")
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()
    }
}
