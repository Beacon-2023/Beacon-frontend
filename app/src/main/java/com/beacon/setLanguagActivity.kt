package com.beacon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat

class setLanguagActivity : AppCompatActivity() {
    private lateinit var btnKorean: AppCompatButton
    private lateinit var btnChinese: AppCompatButton
    private lateinit var btnEnglish: AppCompatButton
    private lateinit var btnThai: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language_main)

        btnKorean = findViewById(R.id.btn_korean)
        btnChinese = findViewById(R.id.btn_중국어)
        btnEnglish = findViewById(R.id.btn_english)
        btnThai = findViewById(R.id.btn_태국어)

        btnKorean.setOnClickListener {
            updateButtonColors(btnKorean)
        }

        btnChinese.setOnClickListener {
            updateButtonColors(btnChinese)
        }

        btnEnglish.setOnClickListener {
            updateButtonColors(btnEnglish)
        }

        btnThai.setOnClickListener {
            updateButtonColors(btnThai)
        }
    }

    private fun updateButtonColors(selectedButton: AppCompatButton) {
        val buttons = arrayOf(btnKorean, btnChinese, btnEnglish, btnThai)

        for (button in buttons) {
            val isSelected = button == selectedButton

            button.setBackgroundResource(
                if (button == selectedButton) {
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
}