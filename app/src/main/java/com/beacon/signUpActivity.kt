package com.beacon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.beacon.databinding.ActivitySignUpBinding
import kotlinx.android.synthetic.main.activity_sign_up.*

class signUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.btnNext.setOnClickListener {
            super.onBackPressed()
        }

        binding.btnNext.isEnabled = false

        var Id = binding.inputId.text.toString()
        var pw = binding.inputPw.text.toString()

        if(Id.length >= 6 && pw.length >= 6){
            binding.btnNext.isEnabled = true
            binding.btnNext.setBackgroundResource(R.drawable.border_radius_box)
        }
    }
}